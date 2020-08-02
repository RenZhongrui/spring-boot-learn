package com.learn.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.time.LocalDateTime;

/***
 * 服务端自定义业务处理handler
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用于记录和管理客户端的channel
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端发来的消息
        String text = msg.text();
        log.error("接收到的数据：" + text);
        TextWebSocketFrame frame = new TextWebSocketFrame(LocalDateTime.now() + "服务器接收到消息：" + text);
        // 遍历客户端
     /*   for (Channel channel : clients) {
            // 将数据回写给客户端，需要TextWebSocketFrame载体
            channel.writeAndFlush(frame);
        }*/
        // 下面的方法同上面的for循环
        clients.writeAndFlush(frame);
    }

    /**
     * 当客户端连接服务端之后（open连接之后）
     * 获取客户端channel并放到ChannelGroup中进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        clients.add(channel);
    }

    // 用户离开浏览器的时候触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved时候，ChannelGroup其实会自动移除，下面的不用写
        // clients.remove(ctx.channel());
        log.error("客户端断开，channel的长id为：" + ctx.channel().id().asLongText());
        log.error("客户端断开，channel的短id为：" + ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.channel().close();
    }
}