package com.learn.netty.netty.fixed;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * create: Ren Zhongrui
 * date: 2020-08-05
 * description:
 */
@Slf4j
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        JSONObject request = (JSONObject) JSONObject.parse(msg.trim());
        log.error("request: " + request.toJSONString());
        if (request != null) {
            if (request.getString("Code") == null) {
                ctx.writeAndFlush(msg);
            } else {
                log.error("客户端执行结果： " + msg.trim());
            }
        }
    }
}