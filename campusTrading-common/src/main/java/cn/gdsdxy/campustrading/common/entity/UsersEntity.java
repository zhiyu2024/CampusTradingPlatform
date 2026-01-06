package cn.gdsdxy.campustrading.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Getter
@Setter
@ToString
@TableName("users")
public class UsersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 所在校区（便于筛选）
     */
    private String campus;

    /**
     * 0-学生, 1-管理员
     */
    private Byte role;

    /**
     * 1-正常, 0-禁用
     */
    private Byte status;

    private Date createdAt;
}
