package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.model.dto.publicDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j // ✅ 添加日志
@Tag(name = "公共接口管理") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/public")
public class publicRegestController {
    @Autowired
    private IUsersService userUsersService;

    /**
     * 注册账号（支持头像上传）
     */
    @PostMapping(value = "/register" , consumes = "multipart/form-data")
    public FwResult<RegisterVo> register(@RequestPart("param") RegisterParam param,
                                         @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        param.setAvatar(avatar);
        return FwResult.ok(userUsersService.registerUser(param));
    }

//    @PostMapping("/register")
//    public FwResult<RegisterVo> Register(@RequestBody RegisterParam registerParam) {
//        RegisterVo registerVo = userUsersService.registerUser(registerParam);
//        return FwResult.ok(registerVo);
//    }
}
