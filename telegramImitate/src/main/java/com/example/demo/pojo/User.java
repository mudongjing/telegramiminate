package com.example.demo.pojo;

import com.example.demo.mybatis.mapper.UserMapper;
import com.example.demo.utils.StrUtils;
import com.example.demo.utils.UserUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * telegram 在mysql中的用户模型
 *
 * 用户好友量表
 * 加入的群组。频道列表
 */
@Data
@NoArgsConstructor
@Table(name="user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer userId;
    protected String userName;
    protected String userPassword;
    protected String userFriends;
    protected Short friendsType;// 0：普通文本，1：本地文件地址存储好友列表
    protected String userGroups;// 自己创建的群组
    protected Short groupType;//0：是字符串表示的列表，1：存储为txt文件表示为集合
    protected String joinGroup;// 加入的群组
    protected Short joinType;
    public User(Integer userId){
        this.userId=userId;
    }
    public User(String userName,String userPassword){
        this.userName=userName;
        this.userPassword= StrUtils.getMd5(userPassword);
        this.friendsType=0;
        this.groupType=0;
    }


}
