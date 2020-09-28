package com.learn.flink.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountApp {
    public static void main(String[] args) throws Exception {
        // 1.获取运行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        String inputFile = "D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-java\\src\\main\\resources\\hello.txt";
        // 2.读取数据
        DataSource<String> inputData = env.readTextFile(inputFile);
        // 3.transform 处理数据 flatMap算子 创建FlatMapFunction函数，
        // 第一个入参表示接受String数据，第二个参数表示返回元组类型数据，元组数据类型是(String, Integer)
        AggregateOperator<Tuple2<String, Integer>> result = inputData.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

            public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
                // 得到的单词数组
                String[] preValues = value.toLowerCase().split(" ");
                for (String val : preValues) {
                    // 对每一个单词做处理，返回单词为键，每一个单词为1的value 的元组
                    collector.collect(new Tuple2<String, Integer>(val, 1));
                }
            }
        }).groupBy(0).sum(1); // 聚合操作：对第一个元素分组，对第二个元素求和
        // 打印
        result.print();
    }
}
