package cn.gdsdxy.campustrading.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = "/test")
    public String test() {
        return "做测试接口"; // "测试成功
    }
}