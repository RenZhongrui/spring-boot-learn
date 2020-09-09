package com.learn.flink.hot

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaProducerUtil {
  def main(args: Array[String]): Unit = {
    writeToKafka("alog-topic")
  }
  def writeToKafka(topic: String): Unit ={
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "47.110.75.209:9092")
    properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](properties)
    // 从文件读取数据，逐行写入kafka
    val bufferedSource = io.Source.fromFile("D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-user-behavior\\ft_hot_items_analysis\\src\\main\\resources\\UserBehavior.csv")
    for( line <- bufferedSource.getLines() ){
      val record = new ProducerRecord[String, String](topic, line)
      producer.send(record)
    }
    producer.close()
  }
}
