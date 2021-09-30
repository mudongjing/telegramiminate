package com.example.redis.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class KeyInRedis {
    private  final String userTable="user_table";
}
