package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j // ✅ 添加日志
@Tag(name = "用户个人管理", description = "用户个人的相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api")
public class UserUsersController {


}
