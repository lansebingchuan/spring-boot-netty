package com.netty.server.handler;

import cn.hutool.core.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

/**
 * <p> 自定义 netty请求处理类 </p>
 * <p> 请求数据处理过程：客户端请求服务端，服务端把数据保存到缓冲区，服务端再从缓冲区拉取数据 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 17:02
 **/
@Slf4j
public class NettyRequestHandler extends SimpleChannelInboundHandler<HttpObject>  {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel注册");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel注册");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel活跃状态");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务端断开连接之后");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel读取数据完毕");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("用户事件触发");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.info("channel可写事件更改");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    //channel发生异常，若不关闭，随着异常channel的逐渐增多，性能也就随之下降
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("捕获channel异常");
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("助手类添加");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("助手类移除");
        super.handlerRemoved(ctx);
    }

    /**
     * 对一个通道进行数据响应
     *
     * @param channelHandlerContext 通道上下文
     * @param httpObject 消息对象
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        // 获取具体的通道
        Channel channel = channelHandlerContext.channel();
        // 判断消息体 是不是一个 http 请求
        if (httpObject instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) httpObject;
//            FullHttpRequest request = (FullHttpRequest) httpRequest;
            String clientIP = this.getClientIP(channelHandlerContext, request);
            log.info("客户端远程连接地址：{}", clientIP);
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            // 获取请求数据
            String contentData = getContentData(request, CharsetUtil.UTF_8);
            log.info("得到客户端请求数据：{}", contentData);
            boolean sendFlag = NettyResponseHandler.responseMsg(channelHandlerContext, "我叫->Austin", keepAlive);
            log.info("服务器返回-》：{}", sendFlag);
        }
    }

    /**
     * 获取请求数据
     *
     * @param httpRequest 请求体
     * @param encoding 请求编码
     * @return 请求数据体
     */
    public String getContentData(HttpRequest httpRequest, String encoding) {
        HttpContent httpContent = (HttpContent)httpRequest;
        ByteBuf contentByteBuf = httpContent.content();
        contentByteBuf.readableBytes();
        return getHttpContentAsString(contentByteBuf, encoding);
    }

    /**
     * 从缓存中获取数据
     *
     * @param bytebuf 缓存体
     * @param encoding 编码方式
     * @return 数据字符串
     */
    public String getHttpContentAsString(ByteBuf bytebuf, String encoding) {
        StringBuffer result = new StringBuffer();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String line = "";
        try {
            bytebuf.readerIndex(0);
            inputStreamReader = new InputStreamReader(new ByteBufInputStream(bytebuf), encoding);
            bufferedReader = new BufferedReader(inputStreamReader);
            while((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\r\n");
            }
        } catch (IOException var15) {
            log.error("Get Http Content Failed!", var15);
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException var14) {
                log.error("System.IOException", var14);
            }

        }
        return result.toString().trim();
    }

    /**
     * 获取客户端请求的ip地址
     *
     * @param ctx 请求通道
     * @param httpRequest 请求体
     * @return ip地址
     */
    private String getClientIP(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        String clientIP = httpRequest.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress insocket = (InetSocketAddress)ctx.channel().remoteAddress();
            clientIP = insocket.getAddress().getHostAddress();
        }
        return clientIP;
    }

}
