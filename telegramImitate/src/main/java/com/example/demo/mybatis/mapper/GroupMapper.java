package com.example.demo.mybatis.mapper;

import com.example.demo.pojo.Group;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    Group getGroup(String name);
    int updateMessageTableAndMemberFile(Group group);
    Integer getGroupId(String name);
    int insertGroupItem(Group group);
    int insertGroupMembers(Integer member);
    Integer queryGroupName(String name);// 查找是否存在已有的群组名
    String queryMember(String name);// 获取 某群组的成员信息，此时隐含
    Integer getCreator(String name);
}
