package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="groupItemTable")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer groupId;
    protected String groupName;
    protected Integer groupCreator;
    protected String groupMembers; // 表示文件地址，为txt文件内部为集合类型
    protected String groupMessage;// 每个群组的消息对应一个单独的数据表
    protected String groupDescription;//群组描述
}
