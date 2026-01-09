package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.model.dto.publicDto.LoginParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j // ✅ 添加日志
@Tag(name = "公共接口管理") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/public")
public class publicLoginController {

    @Autowired
    private IUsersService userUsersService;

    @PostMapping("/login")
    public FwResult<LoginVo> login(@RequestBody LoginParam loginParam) {
        LoginVo loginVo = userUsersService.login(loginParam);
        return FwResult.ok(loginVo);
        // 简单粗暴的 try-catch，更好的做法是配合 @RestControllerAdvice 全局异常处理
//        try {
//            LoginVo loginVO = userUsersService.login(loginParam);
//            return FwResult.ok(loginVO);
//        } catch (RuntimeException e) {
//            return FwResult.fail(e.getMessage());
//        }
    }


}
