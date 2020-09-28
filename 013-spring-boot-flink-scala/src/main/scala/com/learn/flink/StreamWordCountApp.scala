package com.learn.flink
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.DataStream

object StreamWordCountApp {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    val inputPath: String = "D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-scala\\src\\main\\resources\\hello.txt"
    val inputStream: DataStreamSource[String] = env.readTextFile(inputPath);
  }

}
