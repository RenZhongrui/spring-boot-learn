package com.learn.flink.wc;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

public class WordCountKafkaApp {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "47.110.75.209:9092");
        properties.setProperty("group.id","topic-group");
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        DataStreamSource<String> stringDataStreamSource = env.addSource(new FlinkKafkaConsumer011<String>("alog-topic",
                new SimpleStringSchema(), properties));
        stringDataStreamSource.print();
        try {
            env.execute("kafka-wc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
