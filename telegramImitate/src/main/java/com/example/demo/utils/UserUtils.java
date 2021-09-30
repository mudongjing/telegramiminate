package com.example.demo.utils;

import com.example.demo.mybatis.mapper.GroupMapper;
import com.example.demo.mybatis.mapper.GroupMessageMapper;
import com.example.demo.mybatis.mapper.UserMapper;
import com.example.demo.pojo.Group;
import com.example.demo.pojo.User;
import com.example.demo.pojo.dir.Dirc;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;

@Component
public class UserUtils {
    private Logger logger = LoggerFactory.getLogger(UserUtils.class);
    @Autowired
    private Dirc dirc;
    @Resource
    private UserMapper userMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupMessageMapper groupMessageMapper;
    @Autowired
    private GroupUtils groupUtils;

    public User getUser(String userName){
        return userMapper.getUser(userName);
    }
    /**
     * 注册一个新用户
     * @param user
     * @return
     */
    public String registerUser(User user){
        int i=userMapper.registerUser(user);
        if (i==1) return "注册成功";
        else return "注册失败";
    }

    /**
     * 导入进来的是用户名
     * 但实际我们需要插入到群组的是用户id，因此需要先提取id
     * @param userName
     * @param groupName
     * @param description
     * @return
     */
    public String createGroup(String userName,String groupName,String description){
        //Integer userId=userMapper.queryUserId(userName);
        User user=userMapper.getUser(userName);
        if(user==null) return "不存在该用户";
        else{
            Integer i=groupMapper.queryGroupName(groupName);
            if(i!=0) return "当前群名已存在，请更换群组名";
            else{
                /**
                 * 还需要按照格式完成消息表的创建
                 * 作为群组的消息表，名字是 group_groupId
                 * 同时，也需要建立成员文本文件
                 */
                Group group=new Group(null,groupName,user.getUserId(),null,null,description);
                int t=groupMapper.insertGroupItem(group);
                // 在用户的创建群组列表中添加当前群组的id
                groupInsertUserCreate(user,groupName);// 顺便完成消息表的创建和添加
                if(t==0) return "群组创建失败";
                else return "已完成群组创建";
            }
        }
    }

    /**
     * 加入一个不是自己创建的群组
     * @param userName
     * @param groupName
     * @return
     */
    @SneakyThrows
    public String joinGroup(String userName,String groupName){
        /** 利用群组名查到群组的id，然后加入自己的群组列表中，群组也同样做加入成员列表的操作
         * 先确定自己是否已经加入，或者是群组的创建者
         */
        User user=userMapper.getUser(userName);
        Group group=groupMapper.getGroup(groupName);
        if(user==null) return "不存在该用户";
        else {
            if(group==null) return "不存在该群组";
            else{
                int userId=user.getUserId();
                // 用户是否是创建者
                if(group.getGroupCreator()==userId) return "当前用户就是群组的创建者";
                String memberFile=group.getGroupMembers();
                File file=new File(memberFile);
                BufferedReader reader=new BufferedReader(new FileReader(file));
                String line;
                while((line=reader.readLine())!=null){
                    String s=line.replace(" ","");
                    if(!s.equals("") && Integer.parseInt(s)==userId) return "当前用户已加入群组";
                }
                reader.close();
                BufferedWriter writer=new BufferedWriter(new FileWriter(file,true));
                writer.write(userId+"\n");
                writer.close();
                /** 将该群组添加到用户的加入群组列表中
                 * 先提取joinGroup
                 *      joinType // 0 表示就是字符串，1 表示是文本路径
                 *      根据条件读取，
                 *      如果是字符串，还需要提前判断之后的总字符串长度是否会超过255
                 *      否则，构建文本文件存储
                 */
                groupInsertToUserJoin(user,group.getGroupId());
                return "用户-"+userId+"添加到群组"+groupName;
            }
        }
    }

    public String leaveGroup(String userName,String groupName){
        /**
         * 用户退出群组，分为创建者和普通用户
         * 创建者，则整个删除群组，包括群组以下的各个信息，同时，其中的用户，也需要在自己的记录中删除该群组
         * 普通用户，仅删除自己加入的群组列表，群组，删除这样一个成员
         */
        User user=userMapper.getUser(userName);
        Group group=groupMapper.getGroup(groupName);
        if(user.getUserId()==group.getGroupCreator()){
            // 创建者
            deleteGroupFromUser(user,group,true);//用户移除群组信息
            groupUtils.deleteGroupSelf(group);// 销毁群组
            return "创建者删除群组";
        }else{
            // 普通用户，还需要测试，是否属于该群组
            deleteGroupFromUser(user,group,false);//用户移除群组信息
            groupUtils.deleteUserFromGroup(user,group);
            return "用户离开群组";
        }
    }


    /***************
     * 半私有化方法  *
     ***************/

    /**
     * 从用户中删除关于群组的信息，并区分创建者和普通成员
     * @param user
     * @param group
     * @param isCreator
     */
    @SneakyThrows
    public void deleteGroupFromUser(User user,Group group,boolean isCreator){
        String userGroups=null;
        Short type;
        if(isCreator)  {
            userGroups=user.getUserGroups();
            type=user.getGroupType();
        }
        else {
            userGroups=user.getJoinGroup();
            type=user.getJoinType();
        }
        if(userGroups==null) userGroups="";
        userGroups=userGroups.trim();
        String sid=""+group.getGroupId();
        if(type==0){
            // 字符串，删除对应字符串
            userGroups=userGroups.replace(" ","");
            String[] strings=userGroups.split(",");
            String newString="";
            for (String s: strings) {
                if(s.equals(sid)) continue;
                else newString+=s+",";
            }
            newString=StringUtils.chop(newString);
            if(isCreator) user.setUserGroups(newString);
            else user.setJoinGroup(newString);
            userMapper.updateByPrimaryKeySelective(user);// baseMapper的方法
        }else if(type==1){
            // 全部读入，在全部写入，同时检查是否数据降到255以下，但此时，
            // 最好等到讲到150以下，在回到字符串，避免在字符串和文件间变换
            File filePath=new File(userGroups);
            BufferedReader reader=new BufferedReader(new FileReader(filePath));
            ArrayList<String> strings=new ArrayList<>();
            String line=null;
            int num=0;
            while((line=reader.readLine())!=null){
                if(line.equals(sid)) continue;
                else {
                    strings.add(line);
                    num+=line.length()+1;
                }
            }
            reader.close();
            if(num>150){// 还是比较多,仍然是文件
                BufferedWriter writer=new BufferedWriter(new FileWriter(filePath));
                for(String s:strings) { writer.write(s+"\n"); }
                writer.close();
            }else{// 考虑回到字符串
                String res="";
                for (String s: strings) { res+=s+","; }
                res=StringUtils.chop(res);
                if(isCreator){
                    user.setGroupType(new Short("0"));
                    user.setUserGroups(res);
                }else{
                    user.setJoinType(new Short("0"));
                    user.setJoinGroup(res);
                }
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
    }

    /***************************
     * 以下均属于私有方法
     ***************************/

    @SneakyThrows
    private void groupInsertUserCreate(User user,String groupName){
        String groupList=user.getUserGroups();
        if(groupList==null) groupList="";
        else groupList=groupList.trim();
        Short type=user.getGroupType();
        Group group = groupMapper.getGroup(groupName);

        if(group==null) logger.info("groupInsertUserCreate"+"未找到群组");
        String sid=""+group.getGroupId();

        //创建消息表
        // 创建群组成员文本文件
        groupMessageMapper.createGroupMessageTable("group_"+sid);
        group.setGroupMessage("group_"+sid);
        String memberFile=dirc.getGroupMembers()+sid+".txt";
        File file=new File(memberFile);
        file.createNewFile();
        group.setGroupMembers(memberFile);
        groupMapper.updateMessageTableAndMemberFile(group);

        if(type==0){
            groupList=groupList.replace(" ","");
            if(groupList.length()+sid.length()+1>255){
                // 转化为文本
                String[] strings=groupList.split(",");
                user.setGroupType(new Short("1"));
                String newFile=dirc.getCreatedGroupList()+"createdGroup_"+user.getUserId();
                stringInsertToNewFile(strings,newFile,sid);
                user.setUserGroups(newFile);
            }else{
                // 简单在尾部加上
                groupList=groupList.equals("") ? sid : groupList+","+sid;
                user.setUserGroups(groupList);
            }
            userMapper.updateCreatedGroup(user);
        }else if(type==1){
            // 作为文件写入
            File filePath=new File(groupList);
            BufferedWriter writer=new BufferedWriter(new FileWriter(filePath,true));
            writer.write(sid+"\n");
            writer.close();
        }

    }

    @SneakyThrows
    private void groupInsertToUserJoin(User user,Integer groupId){
        String groupList=user.getJoinGroup();
        if(groupList==null) groupList="";
        Short type=user.getJoinType();
        String sid=""+groupId;
        if(type==0){
            groupList=groupList.replace(" ","");
            if(groupList.length()+sid.length()+1>255){// 将超过255，需转为文本文件
                user.setJoinType(new Short("1"));
                String file=dirc.getJoinGroupList()+"joinGroupList_"+user.getUserId();
                String[] strings=groupList.split(",");
                stringInsertToNewFile(strings,file,sid);
                user.setJoinGroup(file);
            }else{//仅在后面添加
                groupList=groupList.equals("") ? ""+sid:groupList+","+sid;
                user.setJoinGroup(groupList);
            }
            //将新的记录提交，使用update
            userMapper.updateJoinGroup(user);
        }else if(type==1){//向文本尾部添加
            groupList=groupList.trim();
            File file=new File(groupList);
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,true));
            writer.write(sid+"\n");
            writer.close();
        }

    }

    @SneakyThrows
    private void stringInsertToNewFile(String[] strings,String newFile,String newMessage){
        File file=new File(newFile);
        file.createNewFile();
        BufferedWriter writer=new BufferedWriter(new FileWriter(newFile));
        for (String s:strings) {
            writer.write(s+"\n");
        }
        writer.write(newMessage+"\n");
        writer.close();
    }


    public static void main(String[] args) {
        ArrayList<String> strings=new ArrayList<>();
        strings.add("adad");
        strings.add("ffff");
        String s="";
        for (String si: strings) {
            s+=si+",";
        }
        s= StringUtils.chop(s);
        System.out.println(s);
    }
}
