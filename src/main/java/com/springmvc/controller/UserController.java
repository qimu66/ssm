package com.springmvc.controller;


import com.springmvc.results.Code;
import com.springmvc.results.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping("/save/{id}")
    @ResponseBody
    public Result save(@PathVariable Integer id) {
        //模拟业务层方法返回
        boolean flag = true;
        //成功返回20011,失败返回20010
        Integer code = Code.SAVE_OK;
        return new Result(code, flag);
    }
}
