package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.config.SecurityConfig;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.UpdateUserParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.UserInfoVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import cn.gdsdxy.campustrading.common.util.JwtFilter;
import cn.gdsdxy.campustrading.common.util.JwtUtils;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/api/user")
public class UserUsersController {
    @Autowired
    IUsersService iUsersService;



    /**
     * 获取个人信息（自动从Token取userId）
     */
    @GetMapping("/info")
    public FwResult<UserInfoVo> getUserInfo() {
        return FwResult.ok(iUsersService.getUserInfo());
    }

    /**
     * 修改资料（支持头像上传）
     */
    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public FwResult<String> updateUserInfo(@RequestPart("param") UpdateUserParam param,
                                           @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        param.setAvatar(avatar);
        iUsersService.updateUserInfo(param);
        return FwResult.ok("修改成功");
    }


    /**
     * 退出登录（自动从请求头获取Token）
     */
    @PostMapping("/logout")
    public FwResult<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        iUsersService.logout(token);
        return FwResult.ok("退出成功");
    }

}
