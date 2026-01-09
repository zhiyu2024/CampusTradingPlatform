package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.model.vo.adminVo.DashboardVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j // ✅ 添加日志
@Tag(name = "管理员数据管理", description = "管理员数据管理相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    IUsersService iUsersService;

    @GetMapping("/stats")
    public FwResult<DashboardVo> getDashboardData() {
        DashboardVo data = iUsersService.getDashboardData();
        return FwResult.ok(data);
    }
}