package com.example.netty.queue.mq;

import com.example.demo.mybatis.mapper.UserMapper;
import com.example.netty.handler.UserInfoManager;
import com.example.netty.pojo.UserInfo;
import com.example.netty.pojo.simple.SimpleUser;
import com.example.netty.proto.ChatReceipt;
import com.example.netty.queue.QueueTransmit;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class CustomQueueTransmit implements QueueTransmit {
    public static Map<Integer, List<String>> groupMap = new ConcurrentHashMap<>();
    public static Map<String, List<String>> infoMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Channel, UserInfo> userInfos = UserInfoManager.userInfos;
    private List<Channel> channels = new Vector<>();
    private static Integer NUMBER=1;
    @Resource
    private UserMapper userMapper;

    @Override
    public void register(Channel channel) {
        channels.add(channel);
    }

    @PostConstruct
    public void init(){
        List<SimpleUser> userList=userMapper.selectAllUserName();
        List<String> groupList=new ArrayList<>();
        for(SimpleUser user:userList){
            infoMap.put(user.getUserName(),new ArrayList<>());
            groupList.add(user.getUserName());
        }
        groupMap.put(NUMBER,groupList);
        pullThread();
    }
    public void pullThread(){
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserInfoManager.rwLock.readLock().lock();
                for (Channel channel : channels) {
                    UserInfo userInfo = userInfos.get(channel);
                    List<String> strings = CustomQueueTransmit.infoMap.get(userInfo.getUserName());
                    for (String string : strings) {
                        channel.writeAndFlush(new TextWebSocketFrame(ChatReceipt.buildMessProto(userInfo.getUserName(), string)));
                    }
                    strings.clear();
                }
                UserInfoManager.rwLock.readLock().unlock();
            }
        }).start();
    }
    @Override
    public void pushGroup( String message) {
        List<String> list = CustomQueueTransmit.groupMap.get(NUMBER);
        message = "[来自于群组" + NUMBER + "的消息]:" + message;
        for (String s : list) {
            List<String> strings = CustomQueueTransmit.infoMap.get(s);
            strings.add(message);
        }
    }
}
