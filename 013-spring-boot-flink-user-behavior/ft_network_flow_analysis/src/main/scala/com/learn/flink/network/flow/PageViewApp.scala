package com.learn.flink.network.flow

import org.apache.flink.api.common.functions.{AggregateFunction, MapFunction}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import scala.util.Random

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

// 定义输出统计的样例类
case class PVCount(windowEnd: Long, count: Long)

// PV统计
object PageViewApp {
  def main(args: Array[String]): Unit = {
    // 1. 初始化环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // 2. 读取输入源
    val resource = getClass.getResource("/UserBehavior.csv");
    val inputStream: DataStream[String] = env.readTextFile("D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-user-behavior\\ft_network_flow_analysis\\src\\main\\resources\\UserBehavior.csv")
    // 3.转化为样例类
    val dataStream: DataStream[UserBehavior] = inputStream
      .map(data => {
        val arr = data.split(",")
        // 转化为样例类
        UserBehavior(arr(0).toLong, arr(1).toLong, arr(2).toInt, arr(3), arr(4).toLong)
      })
      .assignAscendingTimestamps(_.timestamp * 1000L) // 处理顺序数据
    // 4、开窗 聚合 排序
    val pvStream = dataStream
      .filter(_.behavior == "pv")
      // .map(data => ("pv", 1L))  // 会把数据放到一起，全局桶，并行度为1 ，下面处理数据倾斜问题，利用系统的并行度
      .map(new MyMapper) // 为了优化并行度，自定义key进行分组，相当于分成了4（cpu核数）组，分别统计，下一步还需要sum求和
      .keyBy(_._1)
      .timeWindow(Time.hours(1))
      .aggregate(new PVCountAgg(), new PVCountWindowResult())

    val totalPVStream = pvStream
      .keyBy(_.windowEnd)
      // .sum("count") // 根据count进行求和
      .process(new TotalPVCountResult())
    totalPVStream.print()
    env.execute("PageViewApp")
  }
}

// 定义输入和输出
class MyMapper extends MapFunction[UserBehavior, (String, Long)] {
  override def map(t: UserBehavior): (String, Long) = {
    (Random.nextString(10), 1L)
  }
}

// 自定义预聚合函数
class PVCountAgg extends AggregateFunction[(String, Long), Long, Long] {
  override def createAccumulator(): Long = 0L

  override def add(in: (String, Long), acc: Long): Long = acc + 1

  override def getResult(acc: Long): Long = acc

  override def merge(acc: Long, acc1: Long): Long = acc + acc1
}

// 自定义排序
class PVCountWindowResult extends WindowFunction[Long, PVCount, String, TimeWindow] {

  override def apply(key: String, window: TimeWindow, input: Iterable[Long], out: Collector[PVCount]): Unit = {
    out.collect(PVCount(window.getEnd, input.head))
  }
}

// 输入是聚合好的PVCount   .aggregate(new PVCountAgg(), new PVCountWindowResult())
class TotalPVCountResult extends KeyedProcessFunction[Long, PVCount, PVCount] {
  // 定义一个状态，保存pv总和
  lazy val totalPVState: ValueState[Long] = getRuntimeContext.getState(new ValueStateDescriptor[Long]("pv-state", classOf[Long]))

  override def processElement(value: PVCount, context: KeyedProcessFunction[Long, PVCount, PVCount]#Context,
                              collector: Collector[PVCount]): Unit = {
    // 累加操作
    // 将当前的count获取
    val currentCount = totalPVState.value()
    // 然后加上新来的count值，更新到状态中
    totalPVState.update(currentCount + value.count)
    // 注册定时器
    context.timerService().registerEventTimeTimer(value.windowEnd + 1)
  }

  // 触发定时器
  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Long, PVCount, PVCount]#OnTimerContext, out: Collector[PVCount]): Unit = {
    val total = totalPVState.value()
    out.collect(PVCount(ctx.getCurrentKey, total))
    // 最后清除状态
    totalPVState.clear()
  }
}