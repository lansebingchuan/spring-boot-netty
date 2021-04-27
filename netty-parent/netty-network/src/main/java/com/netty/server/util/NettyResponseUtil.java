package com.netty.server.util;

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

/**
 * <p> 自定义 netty响应处理类 </p>
 * <p> 请求数据处理过程：客户端请求服务端，服务端把数据保存到缓冲区，服务端再从缓冲区拉取数据 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 17:02
 **/
@Slf4j
public class NettyResponseUtil {

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
        //定义发送的消息（不是直接发送，而是要把数据拷贝到缓冲区，通过缓冲区）
        //Unpooed：是一个专门用于拷贝Buffer的深拷贝，可以有一个或多个
        //CharsetUtil.UTF_8：Netty提供
        ByteBuf content = Unpooled.copiedBuffer("Hello Netty", CharsetUtil.UTF_8);

        //构建一个HttpResponse，响应客户端
        FullHttpResponse response =
                /**
                 * params1:针对Http的版本号
                 * params2:状态（响应成功或失败）
                 * params3:内容
                 */
                //HttpVersion.HTTP_1_1：默认开启keep-alive
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        //设置当前内容长度、类型等
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        //readableBytes：可读长度
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        //通过长下文对象，把响应刷到客户端
        ChannelFuture channelFuture = channel.writeAndFlush(response);
        return channelFuture.isSuccess();
    }

}
