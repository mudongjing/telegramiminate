package com.example.demo.mybatis.mapper;

import com.example.demo.pojo.expand.ExpandGroupMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMessageMapper {
    int createGroupMessageTable(String name);
    int insertMessageItem(ExpandGroupMessage expandGroupMessage);
    int dropMessageTable(String name);
}
