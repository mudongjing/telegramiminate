package com.example.netty.pojo;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserInfo {
    private boolean auth;
    private String userName;
    private Integer groupNumber;
    // private Channel channel;
    //private String addr;
}
