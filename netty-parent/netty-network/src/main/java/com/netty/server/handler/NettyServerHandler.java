package com.netty.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p> netty 服务处理类 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 16:45
 **/
@Component
public class NettyServerHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 是否是https
     */
    @Value("${netty.server.isHttps}")
    private String isHttps;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //通过socketChannel去获得对应的管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 是 https 请求做特殊处理
        if ("true".equals(this.isHttps)) {

        }
//        pipeline.addLast("serverCodec", new HttpServerCodec());
        // 服务端请求解码
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // 服务端响应编码
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("timeoutHandler", new ReadTimeoutHandler(60));
        pipeline.addLast("contentCompressor", new HttpContentCompressor());
        pipeline.addLast("chunked", new ChunkedWriteHandler());
        // 自定义响应
        pipeline.addLast("handler", new NettyRequestHandler());
    }

}
