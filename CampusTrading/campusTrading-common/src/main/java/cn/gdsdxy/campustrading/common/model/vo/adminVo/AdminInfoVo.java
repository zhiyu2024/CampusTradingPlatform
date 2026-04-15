package cn.gdsdxy.campustrading.common.model.vo.adminVo;

import lombok.Data;

@Data
public class AdminInfoVo {
    private Integer userId;     // 管理员ID
    private String username;    // 登录账号
    private String nickname;    // 昵称
    private String avatar;      // 头像URL
    private Integer role;       // 角色（固定为1）
    private String token;       // 登录后返回的Token（前端存到请求头）
}