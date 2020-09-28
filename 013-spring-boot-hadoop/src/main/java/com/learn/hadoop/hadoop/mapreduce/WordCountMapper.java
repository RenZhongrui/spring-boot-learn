package com.learn.hadoop.hadoop.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * KEYIN：Map任务读数据的key类型，一般是offset，每行数据起始位置的偏移量，Long，第一行偏移量为0
 * VALUEIN：Map任务读数据的value类型，其实就是一行行的字符串，String
 * KEYOUT： map方法自定义实现输出的key类型，String
 * VALUEOUT：map方法自定义实现输出的value类型，Integer类型
 * <p>
 * Long String都是java类型
 * hadoop自定义类型：序列化和反序列化
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 重写map方法
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        super.map(key, value, context);
        // 把value对应的行数据安装指定的分隔符拆开
        String input = value.toString();
        // 按空格分开
        String[] words = input.split(" ");
        for (String word: words) {
            // 输出类型为("hello", 1)，到reduce里进行累加
            context.write(new Text(word.toLowerCase()),new IntWritable(1));
        }
    }
}
