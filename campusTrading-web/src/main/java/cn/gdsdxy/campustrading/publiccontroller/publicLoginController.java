package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.model.dto.pDto.LoginParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
