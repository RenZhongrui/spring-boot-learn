package com.learn.netty.netty;

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
    WebsocketServer echoServer;

    @Bean
    public void run() {
        try {
            ChannelFuture future = echoServer.start(url, port);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    echoServer.destroy();
                }
            });
            //服务端管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
            future.channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
