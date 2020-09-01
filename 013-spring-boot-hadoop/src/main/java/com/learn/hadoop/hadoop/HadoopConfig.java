package com.learn.hadoop.hadoop;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
public class HadoopConfig {

    @Value("${hadoop.user}")
    private String user;

    @Value("${hadoop.hdfs}")
    private String uri;

    @Value("${hadoop.apiDir}")
    private String apiDir;

}
