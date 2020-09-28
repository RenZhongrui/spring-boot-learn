package com.learn.flink

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * 温度聚合s分析
 */
// 定义样例类，温度传感器
case class SensorReading(id: String, timestamp: Long, temperature: Double)

object SensorApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    env.setParallelism(1)
    // 读取数据
    val sourceStream = env.readTextFile("D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-scala\\src\\main\\resources\\sensor.txt");
    // 遍历处理，先转化为样例类型
    val modelStream = sourceStream.map(item => {
      // 对每条数据进行处理
      val arr = item.split(",");
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })
    // modelStream.print();
    // 分组聚合，输出每个传感器的最小值
    val aggStream = modelStream
      .keyBy("id") // 根据id进行分组
      .min("temperature") // 按第二个字段或者字段名进行取最小值
    // aggStream.print()

    // 需要输出当前最小温度值以及最近的时间戳，需要用reduce
    val reduceStream = modelStream
      .keyBy("id") // 只有keyBy之后才可以聚合操作，如min，sum，reduce，agg
      .reduce((currentState, newData) => { // 回调函数方式
        // 返回参数2表示：下一个值的时间戳，不是很准备，应该是时间最大值
        // 返回参数3表示：上一个值跟下一个值对比取最小值
        SensorReading(currentState.id, currentState.timestamp.max(newData.timestamp), currentState.temperature.min(newData.temperature))
      })
    //.reduce(new MySensorReduce) // 自定义类方式
    reduceStream.print()
    env.execute("SensorApp")
  }
}

// 自定义reduce类，第一个参数表示当前状态，第二个参数表示要返回的
class MySensorReduce extends ReduceFunction[SensorReading] {
  override def reduce(t: SensorReading, t1: SensorReading): SensorReading =
    SensorReading(t.id, t1.timestamp, t.temperature.min(t1.temperature))
}
