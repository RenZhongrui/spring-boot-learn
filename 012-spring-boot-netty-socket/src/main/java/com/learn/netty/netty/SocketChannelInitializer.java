package com.learn.netty.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class SocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        SocketHandler serverHandler = new SocketHandler();
        // 为监听客户端read/write事件的Channel添加用户自定义的ChannelHandler
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(serverHandler);
    }
}
