package com.example.demo.utils;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class StrUtils {
    @SneakyThrows
    public static String getMd5(String s){
        MessageDigest messageDigest=MessageDigest.getInstance("MD5");
        byte[] bytes=messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder=new StringBuilder();
        for(byte b:bytes){
            stringBuilder.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
        }
        return stringBuilder.toString();
    }

}
