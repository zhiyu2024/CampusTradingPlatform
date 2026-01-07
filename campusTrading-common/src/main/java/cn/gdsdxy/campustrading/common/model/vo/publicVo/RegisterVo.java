package cn.gdsdxy.campustrading.common.model.vo.publicVo;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterVo {
    private Long userId;          // 用户ID
    private String nickname;  // 用户昵称
    private String avatar;    // 头像

}
