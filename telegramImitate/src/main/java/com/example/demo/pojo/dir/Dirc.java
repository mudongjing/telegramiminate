package com.example.demo.pojo.dir;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Dirc {
    private String groupMessageDir="E:\\FFOutput\\test\\";
    private String userMessageFir="E:\\FFOutput\\message\\";
    private String joinGroupList="E:\\Documents\\code\\SQL\\test\\";
    private String createdGroupList="E:\\Documents\\code\\SQL\\groupCreated\\";
    private String groupMembers="E:\\Documents\\code\\SQL\\member\\";
}
