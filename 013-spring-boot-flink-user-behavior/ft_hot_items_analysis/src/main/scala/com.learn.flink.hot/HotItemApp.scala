package com.learn.flink.hot


import java.sql.Timestamp
import java.util.Properties

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.java.tuple.{Tuple, Tuple1}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.util.Collector

import scala.collection.mutable.ListBuffer

/**
 * 定义样例类
 *
 * @param userId     用户id
 * @param itemId     商品id
 * @param categoryId 类别
 * @param behavior   事件类型 pv uv
 * @param timestamp  时间戳 10位是秒的时间戳，13位是毫秒的时间戳
 */
case class UserBehavior(userId: Long, itemId: Long, categoryId: Int, behavior: String, timestamp: Long)

/**
 *
 * @param itemId    点击id
 * @param windowEnd 结束时间
 * @param count     数量统计
 */
case class ItemViewCount(itemId: Long, windowEnd: Long, count: Long)

object HotItemApp {

  def main(args: Array[String]): Unit = {
    // 1. 初始化设置
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    // 设置并行度
    env.setParallelism(1);
    // 设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // 2. 获取输入源
/*    val filePath = "D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-user-behavior\\ft_hot_items_analysis\\src\\main\\resources\\UserBehavior.csv";
    val inputStream: DataStream[String] = env.readTextFile(filePath)*/
    // 从kafka中读取数据
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "47.110.75.209:9092")
    properties.setProperty("group.id", "topic-group")
    val inputStream: DataStream[String]  = env.addSource(new FlinkKafkaConsumer011[String]("alog-topic", new SimpleStringSchema(), properties))

    // 3. 开始将原始数据转化为样例类
    val dataStream: DataStream[UserBehavior] = inputStream
      .map(data => {
        val arr = data.split(",")
        // 转化为样例类
        UserBehavior(arr(0).toLong, arr(1).toLong, arr(2).toInt, arr(3), arr(4).toLong)
      })
      //.assignAscendingTimestamps(_.timestamp * 1000L) // 处理顺序数据
      // 4. 提取数据中时间戳并生成watermark，设置最大乱序时间为30毫秒，表示当前最大的时间减去30毫秒才是当前时间，设置30毫秒先处理大部分数据
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[UserBehavior](Time.milliseconds(30)) {
        // 提取样例类中的时间戳，并转成毫秒
        override def extractTimestamp(t: UserBehavior): Long = t.timestamp * 1000L
      })

    // 5. 聚合操作：得到窗口聚合结果
    val aggStream: DataStream[ItemViewCount] = dataStream
      .filter(_.behavior == "pv") // 先过滤，当前行为类型必须是pv
      .keyBy("itemId") // 按照itemId进行分组，keyBy操作之后，得到的KeyedStream是JavaTuple
      .timeWindow(Time.hours(1), Time.minutes(5)) // 使用滑动窗口，统计一小时内的数据，每5分钟统计一次
      .aggregate(new CountAgg(), new ItemViewWindowResult()) // 聚合操作

    // 6. 排序处理结果
    val resultStream: DataStream[String] = aggStream
      .keyBy("windowEnd") // 根据窗口进行分组， 收集当前窗口内的商品count值
      .process(new TopNHotItems(5))

    // 打印数据
    resultStream.print()
    // 最后执行操作
    env.execute("HotItemApp")
  }

}

/**
 * 自定义预聚合函数
 * IN: 输入是 UserBehavior
 * ACC：状态也是Long
 * OUT: 输出是每次统计Count结果，要传给ItemViewWindowResult
 */
class CountAgg extends AggregateFunction[UserBehavior, Long, Long] {
  // 聚合状态就是当前商品的count值，初始为0
  override def createAccumulator(): Long = 0L

  // 每来一条数据就会调用一次add函数，执行每次加1操作
  override def add(in: UserBehavior, acc: Long): Long = acc + 1

  // 得到最后结果就是acc
  override def getResult(acc: Long): Long = acc

  // merge用在会话窗口中
  override def merge(acc: Long, acc1: Long): Long = acc + acc1
}

/**
 * 需要继承使用scala的WindowFunction，
 * IN：输入的是CountAgg聚合后的输出结果Count
 * OUT: 输出是ItemViewCount
 * KEY：keyBy操作之后，得到的KeyedStream是JavaTuple，所以key值需要用元组
 * WINDOW：滑动窗口TimeWindow
 */
class ItemViewWindowResult() extends WindowFunction[Long, ItemViewCount, Tuple, TimeWindow] {

  // 输出的是ItemViewCount
  override def apply(key: Tuple, window: TimeWindow, input: Iterable[Long], out: Collector[ItemViewCount]): Unit = {
    // keyby之后得到的是一个一个一元组，需要使用java的，然后先将key转成java的，然后获取值就得到了itemId
    val itemId = key.asInstanceOf[Tuple1[Long]].f0;
    val windowEnd = window.getEnd
    val count = input.iterator.next() // count只有一个数据
    out.collect(ItemViewCount(itemId, windowEnd, count))
  }
}

/**
 * KEY:
 * IN: ItemViewCount
 * OUT: 输出字符串进行打印
 *
 * @param topSize
 */
class TopNHotItems(topSize: Int) extends KeyedProcessFunction[Tuple, ItemViewCount, String] {
  // 先定义状态：listState
  private var itemViewCountListState: ListState[ItemViewCount] = _

  override def open(parameters: Configuration): Unit = {
    itemViewCountListState = getRuntimeContext.getListState(new ListStateDescriptor[ItemViewCount]("itemViewCount-list", classOf[ItemViewCount]))
  }

  // 每来一个ItemViewCount就会执行一次该方法
  override def processElement(value: ItemViewCount, context: KeyedProcessFunction[Tuple, ItemViewCount, String]#Context,
                              out: Collector[String]): Unit = {
    // 每来一个数据就直接加入listState
    itemViewCountListState.add(value)
    // 注册一个windowEnd + 1之后触发的定时器，比如9点要结束的window，加1毫秒之后，之前的数据就会全部到齐，并且window也关闭了
    context.timerService().registerEventTimeTimer(value.windowEnd + 1);
  }

  // 当定时器被触发的时候，可以认为所有的窗口统计结果已经到齐，可以排序输出了
  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Tuple, ItemViewCount, String]#OnTimerContext,
                       out: Collector[String]): Unit = {
    // 为了方便排序，定义一个listBuffer 用来保存listState里的所有数据
    val allItemViewCounts: ListBuffer[ItemViewCount] = ListBuffer()
    val iterator = itemViewCountListState.get().iterator()
    while (iterator.hasNext) {
      val next = iterator.next();
      allItemViewCounts += next;
    }
    // 把这次处理数据清除掉
    itemViewCountListState.clear()
    // 按照Count大小排序，默认是升序，需要反转，然后提取前n个数据
    val sortedItemViewCounts = allItemViewCounts.sortBy(_.count)(Ordering.Long.reverse).take(topSize)

    // 将排名信息格式化成String，便于打印可视化展示
    val result: StringBuilder = new StringBuilder
    // timestamp 是定时器触发时间
    result.append("窗口结束时间：").append(new Timestamp(timestamp - 1)).append("\n")
    // 遍历结果sortedItemViewCounts，输出
    for (i <- sortedItemViewCounts.indices) {
      val currentItem = sortedItemViewCounts(i)
      result.append("NO: ").append(i + 1) .append(":\t") // 排名
        .append("商品ID = ").append(currentItem.itemId).append("\t")
        .append("热门度 = ").append(currentItem.count).append("\n")
    }
    result.append("==========================================\n")
    Thread.sleep(1000)
    // 最后返回输出
    out.collect(result.toString())
  }
}