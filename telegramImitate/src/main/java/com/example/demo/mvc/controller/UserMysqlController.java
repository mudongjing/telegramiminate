package com.example.demo.mvc.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.mybatis.service.UserService;
import com.example.demo.pojo.User;
import com.example.demo.pojo.expand.UserAndFriends;
import com.example.demo.pojo.expand.UserMessage;
import com.example.demo.utils.StrUtils;
import com.example.demo.utils.UserUtils;
import com.example.netty.pojo.simple.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class UserMysqlController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserUtils userUtils;

    @ResponseBody
    @GetMapping("user/get/{name}")
    public User getUser(@PathVariable("name")String name){
        return userService.getUser(name);
    }

    @PostMapping("/sql/add")
    public String addUser(User user){
        userService.add(user);
        return user.getUserName()+": id "+user.getUserId();
    }
    @GetMapping("user/register/{name}/{pass}")
    public String register(@PathVariable("name")String name,@PathVariable("pass") String pass){
        User user=new User(name,pass);
//        int i=userService.register(user);
//        if(i==1) return "注册成功";
//        else return "注册失败";
        return userUtils.registerUser(user);
    }
    @GetMapping("user/query/{name}")
    public Integer queryUserId(@PathVariable("name") String name){
        Integer i= userService.queryUserId(name);
        if (i==null) return 0;
        else return i;

    }
    @GetMapping("/sql/get/{id}")
    public User query(@PathVariable("id") Integer id){
        ExecutorService es=Executors.newFixedThreadPool(200);
        return userService.query(id);
    }
    @GetMapping("sql/create/{tableName}")
    public String createTable(@PathVariable("tableName") String tableName){
        if(userService.create(tableName)>=0){
            return "success";
        }else{
            return "false";
        }
    }
    @GetMapping("user/{userId}/{friendId}/{content}")
    public String insertMessage(@PathVariable("userId") Integer userId,
                            @PathVariable("friendId") Integer friendId,
                            @PathVariable("content")String content){
        Long t=System.currentTimeMillis();
        String tableName1="user_message_"+userId+"__"+friendId;
        String tableName2="user_message_"+friendId+"__"+userId;
        String tableName=null;
        int judge1=userService.judgeForTable(tableName1);
        int judge2=userService.judgeForTable(tableName2);
        if(userService.judgeForTable(tableName1)>0) tableName=tableName1;
        if(userService.judgeForTable(tableName2)>0) tableName=tableName2;
        if(tableName==null){
            userService.createMessageTableForFriends(tableName1);
            tableName=tableName1;
        }
        Date date=new Date(t);
        Time time=new Time(t);
        Short type=new Short("0");
        UserMessage userMessage=new UserMessage(tableName,null,content,type,userId,date,time);
        int i=userService.insertMessageForFriends(userMessage);
        if(i>0) return "消息插入成功";
        else return "消息插入失败";
    }
    @GetMapping("/user/password/{userName}")
    public String getUserPassword(@PathVariable("userName")String userName){
        return userService.getUserPassword(userName);
    }
    @GetMapping("/user/has/{name}/{pass}")
    public int hasUser(@PathVariable("name")String name,
                       @PathVariable("pass")String pass){

        return userService.hasUser(name,pass);
    }
    @GetMapping("user/all")
    public String userAll(){
        List<SimpleUser> list=userService.selectAllUserName();
        String json= JSONObject.toJSONString(list);
        return json;
    }


}
