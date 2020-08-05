package com.learn.netty.netty;

import com.learn.netty.netty.fixed.EchoServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    @Value("${netty.url}")
    private String url;

    @Value("${netty.port}")
    private int port;

    @Autowired
    EchoServer echoServer;

    @Bean
    public void run() {
        try {
            echoServer.start( url,port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
