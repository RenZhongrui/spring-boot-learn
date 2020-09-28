package com.learn.flink.wc;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class NCWordCountApp {

    public static void main(String[] args) throws Exception {
        // 1.获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> ds = env.socketTextStream("47.94.80.7", 9999);
        ds.print();
        env.execute("dddd");
    }
}
