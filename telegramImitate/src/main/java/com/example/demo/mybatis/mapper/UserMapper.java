package com.example.demo.mybatis.mapper;

import com.example.demo.pojo.User;
import com.example.demo.pojo.expand.UserMessage;
import com.example.netty.pojo.simple.SimpleUser;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Set;
@Mapper
public interface UserMapper extends BaseMapper<User> {
    User getUser(String userName);
    int updateJoinGroup(User user);
    int updateCreatedGroup(User user);
    int updateUser(User user);
    int createMessageTable(String tableName);
    int insertUserItem(User user);
    // 用户创建与其它用户关联的消息表
    int createForFriends(String tableName);
    Set judgeForTable(String tableName);
    int insertUserMessage(UserMessage userMessage);
    String getUserPassword(String userName);
    int registerUser(User user);
    int modifyPassword(User user);
    int hasUser(User user);
    List<SimpleUser> selectAllUserName();
    int checkDuplicate(String userName);// 检查是否存在对应的用户名
    Integer queryUserId(String userName);//查询是否存在对应的用户id
}
