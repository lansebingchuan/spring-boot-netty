package com.netty.client.util;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p> 自定义 netty响应处理类 </p>
 * <p> 请求数据处理过程：客户端请求服务端，服务端把数据保存到缓冲区，服务端再从缓冲区拉取数据 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 17:02
 **/
@Slf4j
public class NettyRequestUtil {

    private static SimpleDateFormat sdf = null;

    private static Calendar cd = null;

    static {
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        cd = Calendar.getInstance();
    }

    /**
     * netty 请求服务器 并发送内容
     *
     * @param channel 与服务器建立的通道
     * @param msg     请求的消息体
     */
    public static boolean sendMsg(ChannelHandlerContext channel, String msg, boolean keepAlive) {
        // 定义返回的消息，首先保存到缓存区
//        msg = "\n{\n" +
//                "    \"a\": \"张海涛\"\n" +
//                "}";
        ByteBuf content = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

        /**
         * HttpVersion.HTTP_1_1：默认开启keep-alive
         * HttpResponseStatus.OK：状态为 ok
         * content：返回消息 ByteBuf
         */
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost:8888/1", content);
        // 设置响应头
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        // 设置响应长度
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        request.headers().set(HttpHeaderNames.CONTENT_ENCODING, cn.hutool.core.util.CharsetUtil.UTF_8);
        cd.setTimeInMillis(System.currentTimeMillis());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        ChannelFuture channelFuture = channel.channel().writeAndFlush(request);
        Throwable cause = channelFuture.cause();
        log.info("客户端发送消息：{}，status: {}", msg, channelFuture.isSuccess());
        if (ObjectUtil.isNotEmpty(cause)) {
            log.info("客户端连接超时，发送失败！");
        }
        return true;
    }

}
