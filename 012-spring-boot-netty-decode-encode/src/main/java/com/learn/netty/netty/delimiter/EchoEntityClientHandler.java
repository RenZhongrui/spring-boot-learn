package com.learn.netty.netty.delimiter;

import com.learn.netty.netty.entity.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * create: Ren Zhongrui
 * date: 2020-08-05
 * description:
 */
@Slf4j
public class EchoEntityClientHandler extends SimpleChannelInboundHandler<User> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(getUser());
    }

    private User getUser() {
        User user = new User();
        user.setAge(27);
        user.setName("张三");
        return user;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, User user) throws Exception {
        log.error("receive message from server: " + user.toString());
        ctx.writeAndFlush(user);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
