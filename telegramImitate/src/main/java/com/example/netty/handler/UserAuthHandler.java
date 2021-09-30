package com.example.netty.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.netty.pojo.Constants;
import com.example.netty.pojo.UserInfo;
import com.example.netty.proto.ChatCode;
import com.example.netty.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.WebSocketContainer;
import java.util.concurrent.atomic.AtomicInteger;

public class UserAuthHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthHandler.class);

    private WebSocketServerHandshaker webSocketServerHandshaker;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            System.out.println("http连接进入");
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }else if(msg instanceof WebSocketFrame){
            handleWebSocket(ctx,(WebSocketFrame)msg);
        }
    }
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request){
        if (!request.decoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade"))) {
            logger.warn("protobuf don't support websocket");
            ctx.channel().close();
            return;
        }
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(
                Constants.WEBSOCKET_URL, null, true);
        webSocketServerHandshaker = handshakerFactory.newHandshaker(request);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            // 动态加入websocket的编解码处理
            // 真正完成连接建立
            webSocketServerHandshaker.handshake(ctx.channel(), request);
            // 存储已经连接的Channel
            UserInfoManager.addChannel(ctx.channel());
        }
    }

    private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        String message=((TextWebSocketFrame)frame).text();
        JSONObject json=JSON.parseObject(message);
        int code= json.getInteger("code");
        Channel channel=ctx.channel();
        switch(code){
            case ChatCode.AUTH_CODE:
                boolean isSuccess=UserInfoManager.isAuth(channel,json.getString("nick"), json.getString("password"));
                UserInfoManager.sendReceipt(channel,ChatCode.SYS_AUTH_STATE,isSuccess);
                return;
            case ChatCode.MESS_CODE: //普通的消息留给MessageHandler处理
                break;
            default:
                logger.warn("The code [{}] can't be auth!!!", code);
                return;
        }
        //后续消息交给MessageHandler处理
        ctx.fireChannelRead(frame.retain());
    }
}
