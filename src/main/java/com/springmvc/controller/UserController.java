package com.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @RequestMapping("/q")
    @ResponseBody
    public String a() {
        System.out.println("00000");
        return "sjsjjjs";
    }
}
