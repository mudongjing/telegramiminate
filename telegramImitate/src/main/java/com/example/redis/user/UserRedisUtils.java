package com.example.redis.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.pojo.User;
import com.example.demo.pojo.expand.UserRedis;
import com.example.demo.utils.UserUtils;
import com.example.redis.config.RedisConfigFactory;

import com.example.redis.utils.JsonUtil;
import com.example.redis.utils.KeyInRedis;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 内部包含原始的userUtils
 * 当查询用户时，首先在redis中查看，其次到数据库中获取，同时将结果存入redis中
 *
 * redis中的user对象额外包含了 live和 change 字段，以表明信息是否过期或被修改
 */
@Component
public class UserRedisUtils {
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private RedisConfigFactory redisConfigFactory;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private KeyInRedis keyInRedis;

    @SneakyThrows
    public User getUser(String userName){
        String userTableKey=keyInRedis.getUserTable();
        RedisTemplate<String, Object> redisTemplate = redisConfigFactory.getRedisTemplateByDb(0);
        Object object = redisTemplate.opsForHash().get(userTableKey, userName);
        User user;
        if(object==null) {
            user=userUtils.getUser(userName);
            if(user!=null) {
                String userJson=jsonUtil.addJsonField(JSONObject.toJSONString(user),new String[]{"live","change"},
                        new Boolean[]{true,false});
                redisTemplate.opsForHash().put(userTableKey,userName,userJson);
            }
        }else{
            String res=object.toString();
            user=JSONObject.parseObject(res,User.class);
            JSONObject jsonObject=JSON.parseObject(res);
            jsonObject.get("");
        }
        return user;
    }
    public String putUser(){
        String userTableKey=keyInRedis.getUserTable();
        RedisTemplate<String, Object> redisTemplate = redisConfigFactory.getRedisTemplateByDb(0);
        User userpojo=new User("sdsd","fffff");
        String user= JSONObject.toJSONString(userpojo);
        user=jsonUtil.addJsonField(user,"live",true);
        redisTemplate.opsForHash().put(userTableKey,"new1",user);
        return "成功";
    }

    public static void main(String[] args) {
        (new UserRedisUtils()).getUser("s");
    }

    /*
     * Test
     */
    @SneakyThrows
    public UserRedis getUserWith(String name)  {
        User user=userUtils.getUser(name);
        Field field=user.getClass().getDeclaredField("userName");
        field.setAccessible(true);
        System.out.println(field.get(user).toString());
        UserRedis userWithLiveAndChange=new UserRedis();
        BeanUtils.copyProperties(user,userWithLiveAndChange);
        userWithLiveAndChange.setChange(false);
        userWithLiveAndChange.setLive(true);
        return userWithLiveAndChange;
    }
}
