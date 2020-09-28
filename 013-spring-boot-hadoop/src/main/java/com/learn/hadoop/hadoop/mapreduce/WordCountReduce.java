package com.learn.hadoop.hadoop.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * KEYIN：收到的是Mapper的输出键，Text，对应context.write(new Text(word),new IntWritable(1));
 * VALUEIN：收到的是Mapper的输出值，IntWritable
 * KEYOUT：输出的键类型，Text
 * VALUEOUT：输出的值类型，IntWritable
 * Reducer中使用了模板设计模式，先执行run,在run中先执行setup，再执行reduce，最后执行cleanup
 * 模板设计模式是一种行为设计模式，一般是准备一个抽象类，将部分逻辑以具体方法或者具体的构造函数实现，然后声明一些抽象方法，这样可以强制子类实现剩余的逻辑。不同的子类以不同的方式实现这些抽象方法，从而对剩余的逻辑有不同的实现。这就是模板设计模式能达成的功能。
 * 适用于一些复杂操作进行步骤分割、抽取公共部分由抽象父类实现、将不同的部分在父类中定义抽象实现、而将具体实现过程由子类完成。对于整体步骤很固定，但是某些部分易变，可以将易变的部分抽取出来，供子类实现。
 *    角色：
 *      抽象类：实现模板方法、定义算法骨架
 *       体类：实现抽象类中的抽象方法，完成特定的算法
 */
public class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
    /**
     * 处理Mapper传过来的数据
     * ("hello", 1)  ("world", 1)
     * ("hello", 1)  ("world", 1)
     * ("welcome", 1)
     * map的输出到reduce端，是按照相同的key分发到一个reduce上执行
     * 例如：
     * reduce1：("hello", 1) ("hello", 1) =》("hello", [1,1])
     * reduce2：("world", 1) ("world", 1) =》("world", [1,1])
     * reduce3：("welcome", 1) =》("welcome", [1])
     * 所以参数中对应的就是("hello", [1,1]) key和后面的数组values
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        Iterator<IntWritable> iterator = values.iterator();
        while (iterator.hasNext()) {
            // 返回的就是1
            IntWritable next = iterator.next();
            int value = next.get();
            count += value;
        }
    }
}
