package com.example.demo.pojo.expand;

import com.example.demo.pojo.User;
import lombok.Data;

@Data
public class UserRedis extends User {
    protected Boolean live;
    protected Boolean change;
    protected int count=0;
}
