package com.learn.netty.netty.delimiter;

import com.alibaba.fastjson.JSON;

import com.learn.netty.netty.entity.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * create: Ren Zhongrui
 * date: 2020-08-05
 * description:
 */
public class JsonEncoder extends MessageToByteEncoder<User> {

    @Override
    protected void encode(ChannelHandlerContext ctx, User user, ByteBuf buf) throws Exception {
        String json = JSON.toJSONString(user);
        ctx.writeAndFlush(Unpooled.wrappedBuffer(json.getBytes()));
    }
}