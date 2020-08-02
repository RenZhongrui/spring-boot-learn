package com.learn.netty.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 为监听客户端read/write事件的Channel添加用户自定义的ChannelHandler
        ChannelPipeline pipeline = socketChannel.pipeline();

        //=============支持Http===================
        // websocket用到了Http，所以要有Http编码器
        pipeline.addLast(new HttpServerCodec());
        // 对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对HttpMessage进行聚合，聚合成FullHttpRequest和FullHttpResponse
        // 几乎Netty的使用都会用到这个Handler
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

        //=============websocket配置===================
        /**
         * websocket服务处理handler，用于指定客户端访问的路由"/ws",
         * 该handler会帮助处理一些繁复的事情，比如握手 ping+ pone = 心跳，handshaking(ping, pone)
         * 对于websocket来讲，是以frames进行传输的，不同类型对应的frames也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        // 添加自定义handler，用于处理业务逻辑
        pipeline.addLast(new ChatServerHandler());
    }
}
