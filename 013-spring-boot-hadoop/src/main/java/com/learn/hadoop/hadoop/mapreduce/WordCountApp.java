package com.learn.hadoop.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 将Mapper和Reducer结合起来
 */
public class WordCountApp {
    public static void main(String[] args) throws Exception {
        setup();
    }

    public static void setup() throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://www.rui.hadoop.com:8020");
        // 创建一个Job
        Job job = Job.getInstance(configuration);
        // 设置job运行的主类
        job.setJarByClass(WordCountApp.class);
        // 设置自定义的Mapper和Reducer
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReduce.class);
        // 设置Mapper的输出的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        // 设置Reducer的
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 设置输入路径
        FileInputFormat.setInputPaths(job, new Path("/word-count/input/word.txt"));
        // 设置输出路径
        FileOutputFormat.setOutputPath(job, new Path("/word-count/output/wc.txt"));
        // 提交job
        boolean result = job.waitForCompletion(true);
        System.out.println(result);
    }
}
