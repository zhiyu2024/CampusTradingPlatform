package cn.gdsdxy.campustrading.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j // ✅ 添加日志
@Tag(name = "用户消息管理", description = "用户消息相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/user")
public class UserMessageController {
}
