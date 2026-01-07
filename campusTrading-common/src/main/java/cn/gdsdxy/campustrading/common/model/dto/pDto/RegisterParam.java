package cn.gdsdxy.campustrading.common.model.dto.pDto;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterParam {
    private String studentNo;
    private String username;
    private String password;
    private String nickname;
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;
    private String campus;

    /**
     * 0-学生, 1-管理员 默认0
     */
//    private Byte role;
//
//    /**
//     * 1-正常, 0-禁用
//     */
//    private Byte status;
//
//    private Date createdAt;
}
