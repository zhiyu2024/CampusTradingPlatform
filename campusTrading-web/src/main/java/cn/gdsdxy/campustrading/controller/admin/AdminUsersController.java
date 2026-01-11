package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminLoginParam;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.AdminInfoVo;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.AdminRegisterParam;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    IUsersService iAdminService;

    @PostMapping("/login")
    public FwResult<AdminInfoVo> login(@RequestBody AdminLoginParam param) {
        AdminInfoVo adminInfoVo=   iAdminService.AdminLogin(param);
        return FwResult.ok(adminInfoVo);
    }
     @PostMapping("/register")
     public FwResult<String> registerAdmin(@RequestBody AdminRegisterParam param){
         iAdminService.Adminregister(param);
        return FwResult.ok("注册成功");
     }
    @GetMapping("/info")
    public FwResult<AdminInfoVo> getAdminInfo() {
        AdminInfoVo adminInfo = iAdminService.getAdminInfo();
        return FwResult.ok(adminInfo);
    }

    @PostMapping("/logout")
    public FwResult<String> logout() {
        iAdminService.AdminLogout();
        return FwResult.ok("退出成功");
    }

}
