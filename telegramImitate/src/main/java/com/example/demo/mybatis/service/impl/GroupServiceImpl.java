package com.example.demo.mybatis.service.impl;

import com.example.demo.mybatis.mapper.GroupMapper;
import com.example.demo.mybatis.service.GroupService;
import com.example.demo.pojo.Group;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupMapper groupMapper;
    @Override
    public int insertItem(String name) {
        Group group=new Group(null,name,2,"ff","groupMessage","ddfdf");
        return groupMapper.insertGroupItem(group);
    }

    @Override
    public Integer queryGroupName(String name) {
        return groupMapper.queryGroupName(name);
    }

    @Override
    public String queryMember(String name) {
        return groupMapper.queryMember(name);
    }

    @Override
    public Integer getCreator(String name) {
        return groupMapper.getCreator(name);
    }
}
