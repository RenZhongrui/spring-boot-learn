package com.learn.kafka.admin;

import org.apache.kafka.clients.admin.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AdminSample {

     private static String topic_name = "alog-topic-0";
    //private static String topic_name = "alog-topic";

    public static void main(String[] args) {
        // 1、创建客户端
/*        AdminClient adminClient = getAdminClient();
        System.out.println(adminClient);*/
        // 2、创建主题
        createTopic();
        // 3、获取主题列表
        //getTopicList();
        // 4、删除topic
        //deleteTopic();
    }

    /**
     * 创建客户端
     */
    public static AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "123.56.169.252:9092");
        AdminClient adminClient = AdminClient.create(properties);
        return adminClient;
    }

    /**
     * 创建主题
     */
    public static void createTopic() {
        AdminClient adminClient = getAdminClient();
        // 副本因子
        Short rs = 1;
        NewTopic topic = new NewTopic(topic_name, 1, rs);
        // 可以创建多个主题
     /*   List<NewTopic> topics = new ArrayList<>();
        topics.add(topic);*/
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(topic));
        System.out.println("CreateTopicsResult:::"+topics);
    }

    /**
     * 获取topic列表
     */
    public static void getTopicList() {
        try {
            AdminClient adminClient = getAdminClient();
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);
            ListTopicsResult listTopicsResult = adminClient.listTopics(options);
            // ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> names = listTopicsResult.names().get();
            names.stream().forEach(System.out::println);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除topic
     */
    public static void deleteTopic() {
        AdminClient adminClient = getAdminClient();
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(topic_name));
    }

}
