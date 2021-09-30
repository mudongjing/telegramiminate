package com.example.demo.mvc.testController;

import com.example.demo.mybatis.service.GroupService;
import com.example.demo.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserUtils userUtils;
    @GetMapping("/group/create/{groupName}")
    public String cerateGroupItem(@PathVariable("groupName") String groupName){
        int i=groupService.insertItem(groupName);
        if(i>=0) return "success";
        else return "false";
    }
    @GetMapping("group/query/{name}")
    public String queryGroupName(@PathVariable("name") String name){
        Integer i=groupService.queryGroupName(name);
        if (i==0) return "不存在已有的群组";
        else return "当前群组已存在，请更换群组名";
    }
    @PostMapping("group/create")
    public String createGroup(@RequestParam("userName") String userName,
                              @RequestParam("groupName") String groupName,
                              @RequestParam("description") String description){
        return userUtils.createGroup(userName,groupName,description);
    }
    // 用户加入群组
    @GetMapping("group/join/{username}/{groupName}")
    public String joinGroup(@PathVariable("username") String username,@PathVariable("groupName")String groupName){
        //return groupService.queryMember(name);
        return userUtils.joinGroup(username,groupName);
    }
    @GetMapping("group/leave/{user}/{groupName}")
    public String userLeaveGroup(@PathVariable("user")String user,@PathVariable("groupName")String groupName){
        return userUtils.leaveGroup(user,groupName);
    }

    @GetMapping("group/creator/{name}")
    public Integer getCreator(@PathVariable("name")String name){
        return groupService.getCreator(name);
    }
}
