package cn.gdsdxy.campustrading.common.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum FwResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部异常"),

    // 认证授权相关
    UNAUTHORIZED(401, "未登录或Token过期"),
    FORBIDDEN(403, "无权限执行该操作"),

    // 业务相关错误
    USER_NOT_EXIST(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    PARAM_ERROR(1003, "参数错误");

    private final Integer code;
    private final String message;
}
