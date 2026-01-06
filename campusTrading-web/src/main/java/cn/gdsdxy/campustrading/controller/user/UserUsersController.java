package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@RestController
@RequestMapping("/api/user")
public class UserUsersController {
    @Autowired
    IUsersService iUsersService;

    @GetMapping("/mima")
    public boolean mima(){
        UsersEntity user =iUsersService.getById(3);
        String mima=user.getPassword();
        // 1. 初始化 Spring Security 推荐的加密器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isMatch = encoder.matches("123456", mima);
        return isMatch;
    }

}
