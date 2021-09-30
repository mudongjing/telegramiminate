package com.example.demo.mvc.html;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Html {
    @GetMapping("/user/html")
    public String html(){
        return "index";
    }
}
