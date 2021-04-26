package com.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
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
public class NettyResponseHandler {

    private static SimpleDateFormat sdf = null;

    private static Calendar cd = null;

    static {
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        cd = Calendar.getInstance();
    }

    /**
     * netty 响应 内容
     *
     * @param channel 需要响应的通道
     * @param msg 响应的消息体
     */
    public static boolean responseMsg(ChannelHandlerContext channel, String msg, boolean keepAlive) {
        // 定义返回的消息，首先保存到缓存区
        msg = "你好，我是netty服务器发送过来的, data: " + msg;
        ByteBuf content = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

        /**
         * HttpVersion.HTTP_1_1：默认开启keep-alive
         * HttpResponseStatus.OK：状态为 ok
         * content：返回消息 ByteBuf
         */
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        // 设置响应头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 设置响应长度
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_ENCODING, cn.hutool.core.util.CharsetUtil.UTF_8);
        cd.setTimeInMillis(System.currentTimeMillis());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        response.headers().set(new AsciiString("Date"), sdf.format(cd.getTime()));
        if (!keepAlive) {
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return false;
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ChannelFuture channelFuture = channel.writeAndFlush(response);
            log.info("响应结果：{}", channelFuture.isSuccess());
            return channelFuture.isSuccess();
        }
    }

}
