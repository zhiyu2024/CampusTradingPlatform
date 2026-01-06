package cn.gdsdxy.campustrading.common.controller;

import cn.gdsdxy.campustrading.common.controller.vo.LoginVo;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class publicLoginController {
    @Autowired
    IUsersService iUsersService;
    @PostMapping("/login")
    public String UserLogin(@RequestBody LoginVo loginVo){
           return  iUsersService.UserLogin(loginVo);
    }

}
