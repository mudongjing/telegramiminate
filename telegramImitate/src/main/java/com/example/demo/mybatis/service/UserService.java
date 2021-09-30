package com.example.demo.mybatis.service;

import com.example.demo.pojo.Message;
import com.example.demo.pojo.User;
import com.example.demo.pojo.expand.UserAndFriends;
import com.example.demo.pojo.expand.UserMessage;
import com.example.netty.pojo.simple.SimpleUser;

import java.util.List;

public interface UserService {
    User getUser(String name);
    void add(User user);
    User query(Integer id);
    int create(String name);
    int createMessageTableForFriends(String tableName);
    int judgeForTable(String tableName);
    int insertMessageForFriends(UserMessage userMessage);
    String getUserPassword(String userName);
    int hasUser(String name,String pass);
    List<SimpleUser> selectAllUserName();
    int register(User user);
    Integer queryUserId(String name);
}
