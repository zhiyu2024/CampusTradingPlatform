package cn.gdsdxy.campustrading.publiccontroller;

import cn.gdsdxy.campustrading.common.model.dto.pDto.LoginParam;
import cn.gdsdxy.campustrading.common.model.dto.pDto.RegisterParam;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.LoginVo;
import cn.gdsdxy.campustrading.common.model.vo.publicVo.RegisterVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class publicRegestController {
    @Autowired
    private IUsersService userUsersService;

    @PostMapping("/register")
    public FwResult<RegisterVo> Register(@RequestBody RegisterParam registerParam) {
        RegisterVo registerVo = userUsersService.registerUser(registerParam);
        return FwResult.ok(registerVo);
    }
}
