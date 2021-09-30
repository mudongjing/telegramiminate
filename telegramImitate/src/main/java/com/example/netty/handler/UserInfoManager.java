package com.example.netty.handler;

import com.example.demo.mybatis.mapper.UserMapper;
import com.example.demo.pojo.User;
import com.example.netty.pojo.UserInfo;
import com.example.netty.proto.ChatReceipt;
import com.example.netty.queue.QueueTransmit;
import com.example.netty.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class UserInfoManager {
    public static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private static AtomicInteger userCount = new AtomicInteger(0);
    @Resource
    private  UserMapper userMapper;
    private static UserMapper staticUserMapper;
    @Autowired
    private QueueTransmit queueTransmit;
    private static QueueTransmit staticQueueTransmit;
    public static ConcurrentMap<Channel, UserInfo> userInfos = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserInfoManager.class);
    @PostConstruct
    public void init(){
        staticUserMapper=userMapper;
        staticQueueTransmit=queueTransmit;
    }
    public static boolean isAuth(Channel channel,String userName,String password){
        UserInfo userInfo=userInfos.get(channel);
        if(userInfo==null || (userName==null || password==null)) return false;
        if (!channel.isActive()) {
            logger.error("channel is not active, address: {}, nick: {}",  userName);
            return false;
        }
        User user=new User(userName,password);
        int i=staticUserMapper.hasUser(user);
        boolean auth=true;
        if(i!=1) auth=false;
//        boolean auth=userMapper.hasUser(user)==1 ? true: false;
        if(auth){
            userCount.incrementAndGet();
            userInfo.setUserName(userName);
            userInfo.setAuth(true);
            staticQueueTransmit.register(channel);
        }

        return auth;
    }
    public static void sendReceipt(Channel channel,int code,Object message){
        String data=ChatReceipt.buildSystProto(code,message);
        TextWebSocketFrame textWebSocketFrame=new TextWebSocketFrame(data);
        channel.writeAndFlush(textWebSocketFrame);
    }
    public static void addChannel(Channel channel){
        String remoteAddr = NettyUtil.parseChannelRemoteAddr(channel);
        if (!channel.isActive()) {
            logger.error("channel is not active, address: {}", remoteAddr);
        }
        UserInfo userInfo = new UserInfo();
        userInfos.put(channel, userInfo);
    }
    public static UserInfo getUserInfo(Channel channel){
        return userInfos.get(channel);
    }
    public static void broadcastMess( String message) {
        if (!(message == null) || (message.trim().length() <= 0)) {
            try {
                rwLock.readLock().lock();
                staticQueueTransmit.pushGroup(message);
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

}
