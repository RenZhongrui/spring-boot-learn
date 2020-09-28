package com.learn.flink

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

object WordCountApp {

  // Unit表示返回值为空（void）
  def main(args: Array[String]): Unit = {
    // 创建一个批处理执行环境
    def env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    def inputPath: String = "D:\\Learn\\spring-boot-learn\\013-spring-boot-flink-scala\\src\\main\\resources\\hello.txt"
    def inpitDataSet: DataSet[String] = env.readTextFile(inputPath)
    // 对数据进行转化处理统计，先分词，再安装word进行分组，最后进行聚合统计
    // 得到数据类型是(word,count)
    def resultDataSet: DataSet[(String,Int)] = inpitDataSet
      .flatMap(_.split(" ")) // 对每个元素处理
      .filter(_.nonEmpty) // 判断不为空
      .map((_, 1))
      .groupBy(0) // 对第一个元素进行分组，下标是以0开始
       .sum(1) // 对第二个元素进行求和
    resultDataSet.print();
  }
}
