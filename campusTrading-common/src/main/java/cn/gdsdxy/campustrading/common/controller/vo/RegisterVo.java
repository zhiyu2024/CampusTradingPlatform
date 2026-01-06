package cn.gdsdxy.campustrading.common.controller.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterVo {
    private String studentNo;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private String campus;
    private Byte role;
    private Byte status;
    private Date createdAt;

}
