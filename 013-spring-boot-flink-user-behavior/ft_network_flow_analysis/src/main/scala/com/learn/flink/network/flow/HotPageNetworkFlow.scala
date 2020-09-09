package com.learn.flink.network.flow

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor, MapState, MapStateDescriptor}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import scala.collection.mutable.ListBuffer

// 统计分析热门页面流量
// 定义样例类
case class ApacheLogEvent(ip: String, userId: String, timestamp: Long, method: String, url: String)

// 定义窗口聚合结果样例类
case class PageViewCount(url: String, windowEnd: Long, count: Long)

object HotPageNetworkFlow {

  def main(args: Array[String]): Unit = {
    // 1. 配置环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // 2. 配置输入源
    val file = "D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-user-behavior\\ft_network_flow_analysis\\src\\main\\resources\\apache.log";
    val inputStream: DataStream[String] = env.readTextFile(file)

    // 3. 转化为样例类并处理乱序时间，生成水印
    val dataStream: DataStream[ApacheLogEvent] = inputStream
      .map(item => {
        val arr = item.split(" ")
        // 处理时间戳
        val simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss")
        val formatTimestamp = simpleDateFormat.parse(arr(3)).getTime // 得到毫秒数
        ApacheLogEvent(arr(0), arr(1), formatTimestamp.toLong, arr(5), arr(6))
      })
      .filter(data => { // 过滤掉css/js等资源文件
        val pattern = "^((?!\\.(css|js|ttf|ico|svg)$).)*$".r
        (pattern findFirstIn data.url).nonEmpty
      })
      // 处理乱序时间，生成水印，乱序时间间隔设置根据日志数据中的数据最大间隔来定
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[ApacheLogEvent](Time.minutes(1)) {
        override def extractTimestamp(t: ApacheLogEvent): Long = t.timestamp // 这里是毫秒
      })
    // 4. 开窗聚合和排序输出
    val aggStream: DataStream[PageViewCount] = dataStream
      .filter(_.method == "GET")
      .keyBy(_.url)
      .timeWindow(Time.minutes(10), Time.seconds(5)) // 统计10分钟内访问流量，每5秒统计一次
      .allowedLateness(Time.minutes(1)) // 到时间了窗口先不关，延迟1分钟
      .sideOutputLateData(new OutputTag[ApacheLogEvent]("late")) // 再有迟到的数据放置到侧输出流中处理
      .aggregate(new PageViewCountAgg(), new PageViewCountWindowResult())

    // 输出测试
    /*    dataStream.print("data")
        aggStream.print("agg")
        aggStream.getSideOutput(new OutputTag[ApacheLogEvent]("late")).print("late")*/

    val resultStream: DataStream[String] = aggStream
      .keyBy(_.windowEnd) // 通过窗口结束时间分组
      .process(new TopNHotPages(3))

    resultStream.print()
    env.execute("HotPageNetworkFlow")
  }

}

// 聚合
class PageViewCountAgg() extends AggregateFunction[ApacheLogEvent, Long, Long] {
  // 初始化为0
  override def createAccumulator(): Long = 0L

  // 每次加1
  override def add(in: ApacheLogEvent, acc: Long): Long = acc + 1

  override def getResult(acc: Long): Long = acc

  override def merge(acc: Long, acc1: Long): Long = acc + acc1
}

// String可以根据keyby的类型设置
class PageViewCountWindowResult() extends WindowFunction[Long, PageViewCount, String, TimeWindow] {
  override def apply(key: String, window: TimeWindow, input: Iterable[Long], out: Collector[PageViewCount]): Unit = {
    out.collect(PageViewCount(key, window.getEnd, input.iterator.next()))
  }
}

// key是keyby的类型 .keyBy(_.windowEnd) windowEnd是Long类型
class TopNHotPages(topSize: Int) extends KeyedProcessFunction[Long, PageViewCount, String] {

  // 懒加载，不用在open里实例化
  //lazy val pageViewCountStateList: ListState[PageViewCount] = getRuntimeContext.getListState(new ListStateDescriptor[PageViewCount]("PageViewCount-list", classOf[PageViewCount]))
  // 版本2，更换状态，保证数据一致性,key为url（String）value为Count值，long类型
  lazy val pageViewCountMapState: MapState[String, Long] = getRuntimeContext.getMapState(new MapStateDescriptor[String, Long]("PageViewCount-map", classOf[String], classOf[Long]))

  // 处理过程中，每来一条数据就添加进去，并且注册定时器
  override def processElement(value: PageViewCount, context: KeyedProcessFunction[Long, PageViewCount, String]#Context,
                              collector: Collector[String]): Unit = {
    // 版本1 pageViewCountStateList.add(value)
    // 版本2
    pageViewCountMapState.put(value.url, value.count)
    context.timerService().registerEventTimeTimer(value.windowEnd + 1)
    // 版本2 再注册一个定时器，1分钟之后触发allowedLateness(Time.minutes(1)) ，这时窗口已经彻底关闭，不再有聚合结果输出，可以清空状态
    context.timerService().registerEventTimeTimer(value.windowEnd + 60000L)
  }

  // 触发定时器的时候排序
  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Long, PageViewCount, String]#OnTimerContext, out: Collector[String]): Unit = {
    // 版本1
    //定义ListBuffer，将状态list的数据放到里面，然后排序
    /*    val allPageViewCounts: ListBuffer[PageViewCount] = ListBuffer();
        val iterator = pageViewCountStateList.get().iterator()
        while (iterator.hasNext) {
          allPageViewCounts += iterator.next()
        }
        // 提取清空状态
        pageViewCountStateList.clear()

        // 排序
    val sortedPageViewCounts = allPageViewCounts.sortWith(_.count > _.count).take(topSize)

    // 将排名信息格式化成String，便于打印可视化展示
    val result: StringBuilder = new StringBuilder
    // timestamp 是定时器触发时间
    result.append("窗口结束时间：").append(new Timestamp(timestamp - 1)).append("\n")
    // 遍历结果sortedItemViewCounts，输出
    for (i <- sortedPageViewCounts.indices) {
      val currentItem = sortedPageViewCounts(i)
      result.append("NO: ").append(i + 1).append(":\t") // 排名
        .append("页面URL = ").append(currentItem.url).append("\t")
        .append("热门度 = ").append(currentItem.count).append("\n")
    }
    result.append("==========================================\n")
    Thread.sleep(1000)
    // 最后返回输出
    out.collect(result.toString())
        */

    // 版本2
    // 判断定时器触发时间，如果是windEnd时间+1分钟之后，就清空状态,getCurrentKey是按windowEnd分组的，key就是windowEnd
    if (timestamp == ctx.getCurrentKey + 60000L) {
      pageViewCountMapState.clear()
      return
    }
    val allPageViewCounts: ListBuffer[(String, Long)] = ListBuffer();
    val iterator = pageViewCountMapState.entries().iterator()
    while (iterator.hasNext) {
      val entry = iterator.next()
      allPageViewCounts += ((entry.getKey, entry.getValue))
    }
    // 排序
    val sortedPageViewCounts = allPageViewCounts.sortWith(_._2 > _._2).take(topSize)

    // 将排名信息格式化成String，便于打印可视化展示
    val result: StringBuilder = new StringBuilder
    // timestamp 是定时器触发时间
    result.append("窗口结束时间：").append(new Timestamp(timestamp - 1)).append("\n")
    // 遍历结果sortedItemViewCounts，输出
    for (i <- sortedPageViewCounts.indices) {
      val currentItem = sortedPageViewCounts(i)
      result.append("NO: ").append(i + 1).append(":\t") // 排名
        .append("页面URL = ").append(currentItem._1).append("\t")
        .append("热门度 = ").append(currentItem._2).append("\n")
    }
    result.append("==========================================\n")
    Thread.sleep(1000)
    // 最后返回输出
    out.collect(result.toString())
  }
}