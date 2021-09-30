package com.example.redis.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    // 移除作为json字符串中的指定字段
    @SneakyThrows
    public String removeJsonField(String json,String field){
        ObjectNode jsonNode= objectMapper.readValue(json,ObjectNode.class);
        jsonNode.remove(field);
        return objectMapper.writeValueAsString(jsonNode);
    }

    // 添加字段，包含字段名和值
    @SneakyThrows
    public String addJsonField(String json,String field,Object value){
        return originalAddJsonField(json,new String[]{field},new Object[]{value});
    }

    @SneakyThrows
    public String addJsonField(String json,String[] field,Object[] value){
        return originalAddJsonField(json,field,value);
    }

    /***********
     * 私有方法 *
     ***********/

    @SneakyThrows
    private String originalAddJsonField(String json,String[] field,Object[] value){
        ObjectNode jsonNode= objectMapper.readValue(json,ObjectNode.class);
        for(int i=0;i<field.length;i++){
            if(Boolean.class.isInstance(value[i])) jsonNode.put(field[i],(Boolean)value[i]);
            else if(String.class.isInstance(value[i])) jsonNode.put(field[i],(String)value[i]);
            else if(Integer.class.isInstance(value[i])) jsonNode.put(field[i],(Integer)value[i]);
        }
        return objectMapper.writeValueAsString(jsonNode);
    }
}
