package cn.gdsdxy.campustrading.common.model.vo.publicVo;

import lombok.Data;

@Data
public class LoginVo {
    private String token;     // 必须返回 Token
    private Long userId;          // 用户ID
    private String nickname;  // 用户昵称
    private String avatar;    // 头像
}
