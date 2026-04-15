package cn.gdsdxy.campustrading.common.model.vo.userVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVo {//用户信息VO
    private String studentNo;
    private String nickname;
    private String phone;
    private String avatar;
    private String campus;
    private Integer role;
    private Integer status;
    private LocalDateTime createdAt;
}