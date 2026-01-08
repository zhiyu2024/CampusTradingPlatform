package cn.gdsdxy.campustrading.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j // ✅ 添加日志
@Tag(name = "管理员订单管理", description = "管理员订单相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {
}
