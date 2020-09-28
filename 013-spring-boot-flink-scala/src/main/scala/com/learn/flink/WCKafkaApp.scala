package com.learn.flink

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

object WCKafkaApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "47.110.75.209:9092")
    properties.setProperty("group.id", "topic-group")
    val stream: DataStream[String] = env.addSource(new FlinkKafkaConsumer011[String]("alog-topic", new SimpleStringSchema(), properties))
    stream.print();
    env.execute("WCKafkaApp")
  }

}
