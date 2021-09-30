package com.example.demo.utils;

import cn.hutool.core.collection.CollUtil;
import com.example.demo.mybatis.mapper.GroupMapper;
import com.example.demo.mybatis.mapper.GroupMessageMapper;
import com.example.demo.mybatis.mapper.UserMapper;
import com.example.demo.pojo.Group;
import com.example.demo.pojo.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
public class GroupUtils {
    @Resource
    private UserMapper userMapper;
    @Resource
    private GroupMessageMapper groupMessageMapper;
    @Resource
    private GroupMapper groupMapper;
    @Autowired
    private UserUtils userUtils;

    @SneakyThrows
    private void originalDeleteUserFromGroup(User user,Set<User> userSet,Group group){
        boolean isUser= user!=null;
        Set<String> stringSet=new HashSet<>();
        String sid=isUser ? ""+user.getUserId() : "----";
        if(userSet!=null){
            for (User u: userSet) stringSet.add(u.getUserId()+"");
        }
        String members=group.getGroupMembers();
        File filePath=new File(members);
        BufferedReader reader=new BufferedReader(new FileReader(filePath));
        ArrayList<String> strings=new ArrayList<>();
        String line;
        while((line=reader.readLine())!=null){
            if(line.equals(sid) || stringSet.contains(line)) continue;
            else strings.add(line);
        }
        reader.close();
        BufferedWriter writer=new BufferedWriter(new FileWriter(filePath));
        for(String s:strings) writer.write(s);
        writer.close();
    }

    // 从群组中删除成员
    public void deleteUserFromGroup(User user, Group group){
        originalDeleteUserFromGroup(user,null,group);
    }

    public void deleteUserFromGroup(Set<User> userSet, Group group){
        originalDeleteUserFromGroup(null,userSet,group);
    }

    @SneakyThrows
    public void deleteGroupSelf(Group group){
        /*
            先提取所有成员，逐一去除joinGroup中的该群组的信息
            由于是创建者发起的删除，因此创建者自己的工作在UserUtils那里完成
            然后去除成员文件
            再移除消息数据表
            最后移除群组记录
         */
        String members=group.getGroupMembers();
        ArrayList<Integer> mem = new ArrayList<>();
        File filePath=new File(members);
        BufferedReader reader=new BufferedReader(new FileReader(filePath));
        String line;
        while((line=reader.readLine())!=null) mem.add(Integer.parseInt(line));
        reader.close();
        filePath.delete();// 成员文件删除
        User memberUser;
        for (Integer id:mem) {
            memberUser=userMapper.selectByPrimaryKey(new User(id));
            userUtils.deleteGroupFromUser(memberUser,group,false);
        }// 成员群组信息处理结束
        groupMessageMapper.dropMessageTable(group.getGroupMessage());//删除消息表
        groupMapper.deleteByPrimaryKey(group);// 删除群组记录
    }


}
