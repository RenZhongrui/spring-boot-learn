package com.learn.netty.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class SocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 为监听客户端read/write事件的Channel添加用户自定义的ChannelHandler
        ChannelPipeline pipeline = socketChannel.pipeline();
        // new IdleStateHandler(5, 0, 0)该handler代表如果在5秒内没有收到来自客户端的任何数据包（包括但不限于心跳包），将会主动断开与该客户端的连接。
/*        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 0));
        pipeline.addLast("idleStateTrigger", new ServerIdleStateTrigger());*/
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new SocketHandler());
    }
}
