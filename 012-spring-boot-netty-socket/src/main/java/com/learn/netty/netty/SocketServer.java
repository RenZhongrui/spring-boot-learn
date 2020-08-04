package com.learn.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 服务端
 * 1.创建一个ServerBootstrap的实例引导和绑定服务器。
 * 2.创建并分配一个NioEventLoopGroup实例以进行事件的处理，比如接受连接以及读写数据。
 * 3.指定服务器绑定的本地的InetSocketAddress。
 * 4.使用一个EchoServerHandler的实例初始化每一个新的Channel。
 * 5.调用ServerBootstrap.bind()方法以绑定服务器。
 */
@Slf4j
@Component
public class SocketServer {

    /**
     * NioEventLoop并不是一个纯粹的I/O线程，它除了负责I/O的读写之外
     * 创建了两个NioEventLoopGroup，它们实际是两个独立的Reactor线程池。
     */
    // 创建主线程组，用于接收客户端的TCP连接，但是不做任何处理，跟老板一样，不做事
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    // 创建工作线程组，用于处理I/O相关的读写操作，或者执行系统Task、定时任务Task等
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    /**
     * 启动服务
     *
     * @param hostname
     * @param port
     * @return
     * @throws Exception
     */
    public ChannelFuture start(String hostname, int port) {
        ChannelFuture future = null;
        try {
            // ServerBootstrap负责初始化netty服务器，并且开始监听端口的socket请求
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(hostname, port))
                    .childHandler(new SocketChannelInitializer());
            // 绑定端口并同步等待连接
            future = bootstrap.bind().sync();
            channel = future.channel();
            log.info("======EchoServer启动成功!!!=========");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (future != null && future.isSuccess()) {
                log.info("Netty server listening " + hostname + " on port " + port + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }
        return future;
    }

    /**
     * 停止服务
     */
    public void destroy() {
        log.info("Shutdown Netty Server...");
        try {
            if (channel != null) {
                channel.close().sync();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }
}
