package com.example.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.netty.pojo.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        UserInfo userInfo = UserInfoManager.getUserInfo(ctx.channel());
        if(userInfo!=null && userInfo.isAuth()){
            JSONObject jsonObject=JSONObject.parseObject(msg.text());
            String mess=jsonObject.getString("mess");
            if(mess!=null){
                UserInfoManager.broadcastMess(mess);
            }
        }
    }

}
