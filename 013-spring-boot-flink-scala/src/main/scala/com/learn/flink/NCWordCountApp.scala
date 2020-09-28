package com.learn.flink

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object NCWordCountApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    val ds = env.socketTextStream("47.94.80.7", 9999);
    ds.print().setParallelism(1)
    env.execute("NCWordCountAppExecute")
  }
}
