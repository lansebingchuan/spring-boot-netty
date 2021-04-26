package com.netty.client.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p> 客户端接收处理类 </p>
 *
 * @author: ZHT
 * @create: 2021-04-26 15:19
 **/
@Component
public class NettyClientHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 客户端数据处理类
     */
    private static final NettyClientDataHandler nettyClientDataHandler = new NettyClientDataHandler();

    /**
     * 是否是https
     */
    @Value("${netty.server.isHttps}")
    private String isHttps;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 是 https 请求做特殊处理
        if ("true".equals(isHttps)) {

        }
        // 客户端端请求编码
        pipeline.addLast("encoder", new HttpRequestEncoder());
        // 客户端端响应解码
        //pipeline.addLast("decoder", new HttpResponseDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("timeoutHandler", new ReadTimeoutHandler(60));
        pipeline.addLast("contentCompressor", new HttpContentCompressor());
        pipeline.addLast("chunked", new ChunkedWriteHandler());
        // 自定义响应
        pipeline.addLast(nettyClientDataHandler);
    }

    /**
     * 发送消息
     *
     * @param msg 发送的消息
     * @return 发送是否成功
     */
    public boolean sendMsg(String msg) {
        return nettyClientDataHandler.sendMsg(msg);
    }
}
