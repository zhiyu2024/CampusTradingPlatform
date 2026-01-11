package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

@Data
public class AdminRegisterParam {
    private String username;    // 登录账号
    private String password;    // 密码
    private String nickname;    // 昵称
}