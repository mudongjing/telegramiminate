package com.example.demo.mvc.controller;

import com.example.demo.pojo.User;
import com.example.demo.pojo.expand.UserRedis;
import com.example.redis.user.UserRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRedisController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRedisUtils userRedisUtils;

    @GetMapping("/redis/get/{key}")
    public Object get(@PathVariable("key") String key){

        return redisTemplate.opsForValue().get(key);
    }
    @PostMapping("redis/set/{key}/{value}")
    public String set(@PathVariable("key") String key,@PathVariable("value") String value){
        redisTemplate.opsForValue().set(key,value);
        return key+" :"+value;
    }
    @ResponseBody
    @GetMapping("redis/user/get/{name}")
    public User getUser(@PathVariable("name")String name){
        return userRedisUtils.getUser(name);
    }

    @ResponseBody
    @GetMapping("redis/userwithliveandchange/get/{name}")
    public UserRedis getUserWith(@PathVariable("name")String name){
        return userRedisUtils.getUserWith(name);
    }
    @GetMapping("redis/user/put")
    public String putUser(){
        return  userRedisUtils.putUser();
    }
//    public static void main(String[] args) {
//        String s="12,3,4,5";
//        String[] ss=s.split(",");
//        Integer[] a=new Integer[ss.length];
//        for(int i=0;i<ss.length;i++){
//            a[i]=Integer.parseInt(ss[i]);
//            System.out.println(a[i]+a[i].getClass().getName());
//        }
//
//    }
}