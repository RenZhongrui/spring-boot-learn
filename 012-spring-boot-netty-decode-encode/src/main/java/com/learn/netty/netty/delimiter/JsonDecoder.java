package com.learn.netty.netty.delimiter;


import java.util.List;

import com.alibaba.fastjson.JSON;
import com.learn.netty.netty.entity.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * create: Ren Zhongrui
 * date: 2020-08-05
 * description: Json解码，把ByteBuf解码成实体类
 */
@Slf4j
public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String userStr = new String(bytes, CharsetUtil.UTF_8);
        log.error("decode:" + userStr);
        if (!"".equalsIgnoreCase(userStr)) {
            User user = JSON.parseObject(userStr, User.class);
            out.add(user);
        }
    }
}