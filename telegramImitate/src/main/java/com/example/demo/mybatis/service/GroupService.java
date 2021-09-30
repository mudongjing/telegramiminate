package com.example.demo.mybatis.service;

public interface GroupService {
    int insertItem(String name);
    Integer queryGroupName(String name);
    String queryMember(String name);
    Integer getCreator(String name);
}
