package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminLoginParam;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.AdminInfoVo;
import cn.gdsdxy.campustrading.common.model.dto.adminDto.AdminRegisterParam;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "管理员用户管理", description = "管理员用户管理相关接口")
@RestController
@RequestMapping("/api")
public class AdminUsersController {

    @Autowired
    private IUsersService iAdminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/AdminLogin")
    public FwResult<AdminInfoVo> login(@RequestBody AdminLoginParam param) {
        AdminInfoVo adminInfoVo = iAdminService.AdminLogin(param);
        return FwResult.ok(adminInfoVo);
    }

    @PostMapping("/AdminRegister")
    public FwResult<String> registerAdmin(@RequestBody AdminRegisterParam param) {
        iAdminService.Adminregister(param);
        return FwResult.ok("注册成功");
    }

    @GetMapping("/admin/info")
    public FwResult<AdminInfoVo> getAdminInfo() {
        AdminInfoVo adminInfo = iAdminService.getAdminInfo();
        return FwResult.ok(adminInfo);
    }

    @PostMapping("/admin/logout")
    public FwResult<String> logout() {
        iAdminService.AdminLogout();
        return FwResult.ok("退出成功");
    }

    @GetMapping("/admin/users")
    public FwResult<IPage<UsersEntity>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Byte role,
            @RequestParam(required = false) Byte status) {
        log.info("管理员查询用户列表, page={}, pageSize={}, username={}, role={}, status={}", page, pageSize, username, role, status);
        Page<UsersEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UsersEntity> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(UsersEntity::getUsername, username);
        }
        if (role != null) {
            wrapper.eq(UsersEntity::getRole, role);
        }
        if (status != null) {
            wrapper.eq(UsersEntity::getStatus, status);
        }
        wrapper.orderByDesc(UsersEntity::getCreatedAt);
        IPage<UsersEntity> result = iAdminService.page(pageParam, wrapper);
        return FwResult.ok(result);
    }

    @GetMapping("/admin/users/buyers")
    public FwResult<IPage<UsersEntity>> getBuyerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Byte status) {
        log.info("管理员查询买家列表, page={}, pageSize={}, username={}, status={}", page, pageSize, username, status);
        Page<UsersEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UsersEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsersEntity::getRole, 0);
        if (username != null && !username.isEmpty()) {
            wrapper.like(UsersEntity::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(UsersEntity::getStatus, status);
        }
        wrapper.orderByDesc(UsersEntity::getCreatedAt);
        IPage<UsersEntity> result = iAdminService.page(pageParam, wrapper);
        return FwResult.ok(result);
    }

    @GetMapping("/admin/users/sellers")
    public FwResult<IPage<UsersEntity>> getSellerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Byte status) {
        log.info("管理员查询卖家列表, page={}, pageSize={}, username={}, status={}", page, pageSize, username, status);
        Page<UsersEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UsersEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsersEntity::getRole, 0);
        if (username != null && !username.isEmpty()) {
            wrapper.like(UsersEntity::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(UsersEntity::getStatus, status);
        }
        wrapper.orderByDesc(UsersEntity::getCreatedAt);
        IPage<UsersEntity> result = iAdminService.page(pageParam, wrapper);
        return FwResult.ok(result);
    }

    @GetMapping("/admin/users/{id}")
    public FwResult<UsersEntity> getUserDetail(@PathVariable Long id) {
        log.info("管理员查询用户详情, id={}", id);
        UsersEntity user = iAdminService.getById(id);
        if (user == null) {
            return FwResult.fail("用户不存在");
        }
        return FwResult.ok(user);
    }

    @PostMapping("/admin/users")
    public FwResult<String> createUser(@RequestBody UsersEntity user) {
        log.info("管理员创建用户, username={}", user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        iAdminService.save(user);
        return FwResult.ok("创建成功");
    }

    @PutMapping("/admin/users/{id}")
    public FwResult<String> updateUser(@PathVariable Long id, @RequestBody UsersEntity user) {
        log.info("管理员更新用户, id={}", id);
        user.setUserId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            UsersEntity existing = iAdminService.getById(id);
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        }
        iAdminService.updateById(user);
        return FwResult.ok("更新成功");
    }

    @DeleteMapping("/admin/users/{id}")
    public FwResult<String> deleteUser(@PathVariable Long id) {
        log.info("管理员删除用户, id={}", id);
        iAdminService.removeById(id);
        return FwResult.ok("删除成功");
    }
}
