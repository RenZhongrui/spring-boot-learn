package com.learn.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/***
 * 服务端自定义业务处理handler
 */
@Slf4j
public class SocketHandler extends ChannelInboundHandlerAdapter {

    /**
     * 对每一个传入的消息都要调用；
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取Channel
        Channel channel = ctx.channel();
        // 显示远程客户端访问地址
        SocketAddress socketAddress = channel.remoteAddress();
        log.error(String.valueOf(socketAddress));
        // 构建响应数据
        ByteBuf in = (ByteBuf) msg;
        String res = in.toString(CharsetUtil.UTF_8);
        System.out.println("server received: " + res);
        // 返回response数据，先写到缓冲区然后刷新到客户端
        ctx.write(in);
    }


    /**
     * 通知ChannelInboundHandler最后一次对channelRead()的调用时当前批量读取中的的最后一条消息。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 在读取操作期间，有异常抛出时会调用。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}