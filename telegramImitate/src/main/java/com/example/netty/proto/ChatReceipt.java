package com.example.netty.proto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
@Data
public class ChatReceipt {
    public static final int PING_PROTO = 476; //ping消息
    public static final int PONG_PROTO = 732; //pong消息
    public static final int SYST_PROTO = 988; //系统消息
    public static final int EROR_PROTO = 1244; //错误消息
    public static final int AUTH_PROTO = 1500; //认证消息
    public static final int MESS_PROTO = 1756; //普通消息

    private int version = 1;
    private int uri;
    private String body;
    private Map<String,Object> extend = new HashMap<>();

    public ChatReceipt(int head, String body) {
        this.uri = head;
        this.body = body;
    }
    public static String buildSystProto(int code, Object mess) {
        ChatReceipt chatReceipt = new ChatReceipt(SYST_PROTO, null);
        chatReceipt.extend.put("code", code);
        chatReceipt.extend.put("mess", mess);
        String result=JSONObject.toJSONString(chatReceipt);
        return result;
    }
    public static String buildMessProto(String userName, String mess) {
        ChatReceipt chatProto = new ChatReceipt(MESS_PROTO, mess);
        chatProto.extend.put("nick", userName);
        Long t=System.currentTimeMillis();
        chatProto.extend.put("time", (new Date(t)).toString()+(new Time(t)).toString());
        return JSONObject.toJSONString(chatProto);
    }
}
