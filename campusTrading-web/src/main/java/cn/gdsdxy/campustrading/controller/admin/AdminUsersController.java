package cn.gdsdxy.campustrading.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "管理员个人管理", description = "管理员个人管理相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api")
public class AdminUsersController {

}
