package com.learn.netty.netty.fixed;

import com.learn.netty.netty.delimiter.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class EchoServer {

    public void start(String hostname, int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .localAddress(new InetSocketAddress(hostname, port))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 自定义分隔符
               /*             // 这里将FixedLengthFrameDecoder添加到pipeline中，指定长度为20
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
                            // 将前一步解码得到的数据转码为字符串
                            ch.pipeline().addLast(new StringDecoder());
                            // 这里FixedLengthFrameEncoder是我们自定义的，用于将长度不足20的消息进行补全空格
                            ch.pipeline().addLast(new FixedLengthFrameEncoder(20));
                            // 最终的数据处理
                            ch.pipeline().addLast(new EchoServerHandler());*/

                            // 自定义分隔符
        /*                    String delimiter = "_$";
                            // 将delimiter设置到DelimiterBasedFrameDecoder中，经过该解码一器进行处理之后，源数据将会
                            // 被按照_$进行分隔，这里1024指的是分隔的最大长度，即当读取到1024个字节的数据之后，若还是未
                            // 读取到分隔符，则舍弃当前数据段，因为其很有可能是由于码流紊乱造成的
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,
                                    Unpooled.wrappedBuffer(delimiter.getBytes())));
                            // 将分隔之后的字节数据转换为字符串数据
                            ch.pipeline().addLast(new StringDecoder());
                            // 这是我们自定义的一个编码器，主要作用是在返回的响应数据最后添加分隔符
                            ch.pipeline().addLast(new DelimiterBasedFrameEncoder(delimiter));
                            // 最终处理数据并且返回响应的handler
                            ch.pipeline().addLast(new EchoServerHandler());*/

                            // 使用换行符
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            // 将分隔之后的字节数据转换为字符串数据
                            ch.pipeline().addLast(new StringDecoder());
                            // 这是我们自定义的一个编码器，主要作用是在返回的响应数据最后添加分隔符
                            ch.pipeline().addLast(new AgentLineBasedFrameEncoder("\n"));
                            // 最终处理数据并且返回响应的handler
                            ch.pipeline().addLast(new EchoServerHandler());

                            // 这里将LengthFieldBasedFrameDecoder添加到pipeline的首位，因为其需要对接收到的数据
                            // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
    /*                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            // LengthFieldPrepender是一个编码器，主要是在响应字节数据前面添加字节长度字段
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            // 对经过粘包和拆包处理之后的数据进行json反序列化，从而得到User对象
                            ch.pipeline().addLast(new JsonDecoder());
                            // 对响应数据进行编码，主要是将User对象序列化为json
                            ch.pipeline().addLast(new JsonEncoder());
                            // 处理客户端的请求的数据，并且进行响应
                            // 最终处理数据并且返回响应的handler
                            ch.pipeline().addLast(new EchoEntityClientHandler());*/
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
