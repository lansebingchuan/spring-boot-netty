package com.netty.client;

import com.netty.client.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p> netty客户端启动类 </p>
 *
 * @author: ZHT
 * @create: 2021-04-26 15:05
 **/
@Slf4j
@Component
public class NettyClientServer implements Runnable{

    @Autowired
    private NettyClientHandler clientHandler;

    /**
     * 主线程
     */
    private EventLoopGroup mainEventLoop;

    /**
     * 服务启动类
     */
    private Bootstrap bootstrap;

    /**
     * 服务端ip
     */
    @Value("${netty.server.ip}")
    private String serverIp;

    /**
     * 服务端端口
     */
    @Value("${netty.server.port}")
    private Integer serverPort;

    @Override
    public void run() {
        close();
        bootstrap = new Bootstrap();
        mainEventLoop = new NioEventLoopGroup();
        bootstrap.group(mainEventLoop)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(clientHandler);
        try {
            ChannelFuture channelFuture = bootstrap.connect(serverIp, serverPort).sync();
            log.info("Netty客户端启动成功，ip：{}，port：{}", serverIp, serverPort);
            //获取某个客户端所对应的chanel，关闭并设置同步方式
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("Netty客户端启动失败，ip：{}，port：{}", serverIp, serverPort);
        } finally {
            close();
        }
    }

    /**
     * 关闭客户端
     */
    private void close() {
        // 关闭线程通道
        Optional.ofNullable(mainEventLoop).map(EventExecutorGroup::shutdownGracefully);
        log.info("netty客户端已关闭，ip: {}, port: {}！", serverIp, serverPort);
    }

    /**
     * 发送消息
     *
     * @param msg 发送的消息
     * @return 发送是否成功
     */
    public boolean sendMsg(String msg) {
        return clientHandler.sendMsg(msg);
    }
}
