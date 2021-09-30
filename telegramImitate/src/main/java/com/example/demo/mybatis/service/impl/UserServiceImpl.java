package com.example.demo.mybatis.service.impl;

import com.example.demo.mybatis.mapper.UserMapper;
import com.example.demo.mybatis.service.UserService;
import com.example.demo.pojo.Message;
import com.example.demo.pojo.User;
import com.example.demo.pojo.dir.Dirc;
import com.example.demo.pojo.expand.UserAndFriends;
import com.example.demo.pojo.expand.UserMessage;
import com.example.netty.pojo.simple.SimpleUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private Dirc dirc;
    @Override
    public void add(User user) {
        userMapper.insert(user);
    }

    @Override
    public User getUser(String name) {
        return userMapper.getUser(name);
    }

    @Override
    public int register(User user) {
        int i=userMapper.checkDuplicate(user.getUserName());
        if(i==0) return userMapper.registerUser(user);
        return i;
    }

    @Override
    public Integer queryUserId(String name) {
        return userMapper.queryUserId(name);
    }

    @Override
    public User query(Integer id) {
        String userKey="user:"+id;
        Object object=redisTemplate.opsForValue().get(userKey);
        if(object==null){
            synchronized (this.getClass()){
                if((object=redisTemplate.opsForValue().get(userKey)) ==null){
                    log.debug("==== 走数据库查询");
                    User user=userMapper.selectByPrimaryKey(id);
                    redisTemplate.opsForValue().set(userKey,user);
                    return user;
                }else{
                    log.debug("来自redis缓存(同步代码块) 》》》》》》》》》》》");
                    User user=new Gson().fromJson(object.toString(),User.class);
                    return user;
                }
            }
        }else{
            log.debug("来自redis缓存》》》》》》》》》》》");
            User user=new Gson().fromJson(object.toString(),User.class);
            return user;
        }
    }

    @Override
    public int create(String name) {
        return userMapper.createMessageTable(name);
    }

    @Override
    public int createMessageTableForFriends(String tableName) {
        return userMapper.createForFriends(tableName);
    }

    @Override
    public int judgeForTable(String tableName) {
        return userMapper.judgeForTable(tableName).size();
    }

    @Override //完成该函数，实现向表中插入消息，同时判断消息是否为大文本
    public int insertMessageForFriends(UserMessage userMessage) {
        String content=userMessage.getMessageContent();
        Short type=new Short("0");
        if(content.length()>255){
            type=1;
            content=createFileForUserAndFriendsMessage(userMessage);
            userMessage.setMessageContent(content);
            userMessage.setMessageType(type);
        }
        return userMapper.insertUserMessage(userMessage);
    }
    @SneakyThrows
    private String createFileForUserAndFriendsMessage(UserMessage userMessage){
        String content=userMessage.getMessageContent();
        String tableName=userMessage.getTableName();
        String way=dirc.getUserMessageFir()+tableName+".txt";
        File file=new File(way);
        file.createNewFile();
        BufferedWriter out=new BufferedWriter(new FileWriter(file));
        out.write(content);out.close();
        return way;
    }

    @Override
    public String getUserPassword(String userName) {
        return userMapper.getUserPassword(userName);
    }

    @Override
    public int hasUser(String name, String pass) {
        User user=new User(name,pass);
        int i=userMapper.hasUser(user);
        return i;
    }

    @Override
    public List<SimpleUser> selectAllUserName() {
        return userMapper.selectAllUserName();
    }
}
