package com.du;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONNECTION;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_LENGTH;

/**
 * @Title
 * @ClassName HttpServerHandler
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter  {
    Logger logger = Logger.getLogger("HttpServerHandler");
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        Map<String,Object> retMap ;
        try {
            retMap = Route.checkRouteUri(msg);
        }catch (Exception ex){
            ex.printStackTrace();
            retMap = new HashMap<>();
        }

        String m = retMap.get("state")==null?"未知错误":retMap.get("state").toString();
        if(Route.SUCCESS.equals(m)){
            m = retMap.get("data")==null?"暂无数据":retMap.get("data").toString();
        }

        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK, Unpooled.wrappedBuffer(m
                .getBytes()));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH,
                response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
