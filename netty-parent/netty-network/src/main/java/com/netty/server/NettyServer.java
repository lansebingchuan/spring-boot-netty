package com.netty.server;

import com.netty.server.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p> Netty服务端 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 16:16
 **/
@Slf4j
@Component
public class NettyServer implements Runnable {

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Value("${netty.server.port}")
    private Integer nettyPort;

    @Override
    public void run() {
        startNettyServer();
    }

    private void startNettyServer() {
        log.info("netty服务开始启动,端口：{}", nettyPort);
        // 定义主线程组，用于接收客户端的链接，但不做任何处理
        EventLoopGroup mainGroup = new NioEventLoopGroup();

        // 定义从线程组，主线程组会把任务转给，从线程组进行处理
        EventLoopGroup workGroup = new NioEventLoopGroup();
        // 服务启动类
        ServerBootstrap bootstrap = new ServerBootstrap()
                // 启动构建 主线程组与从线程组
                .group(mainGroup, workGroup)
                // 设置为 nio 双向通道
                .channel(NioServerSocketChannel.class)
                // 定义接收通道消息 处理类
                .childHandler(nettyServerHandler)
                //设置队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true);;
        try {
            // 绑定端口并启动
            ChannelFuture syncFuture = bootstrap.bind(nettyPort).sync();

            //关闭 获取某个客户端所对应的chanel，关闭并设置同步方式
            syncFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("Netty服务启动端口：{} 失败", nettyPort);
        } finally {
            // 关闭线程通道
            Optional.of(mainGroup).map(EventExecutorGroup::shutdownGracefully);
            Optional.of(workGroup).map(EventExecutorGroup::shutdownGracefully);
        }
        log.info("netty服务启动成功，绑定端口：{}！", nettyPort);
    }


}
