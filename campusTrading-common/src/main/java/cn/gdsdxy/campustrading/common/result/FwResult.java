package cn.gdsdxy.campustrading.common.result;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应结果封装
 * @param <T> 数据类型
 */
@Data
public class FwResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 数据载体
     */
    private T data;

    // 私有构造方法，禁止外部 new
    private FwResult() {}

    // ============================ 成功响应 ============================

    /**
     * 成功（无数据）
     */
    public static <T> FwResult<T> ok() {
        return build(FwResultCode.SUCCESS.getCode(), FwResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> FwResult<T> ok(T data) {
        return build(FwResultCode.SUCCESS.getCode(), FwResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功（自定义消息 + 数据）
     */
    public static <T> FwResult<T> ok(String msg, T data) {
        return build(FwResultCode.SUCCESS.getCode(), msg, data);
    }

    // ============================ 失败响应 ============================

    /**
     * 失败（使用默认错误码 500）
     */
    public static <T> FwResult<T> fail(String msg) {
        return build(FwResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 失败（指定错误码枚举）
     */
    public static <T> FwResult<T> fail(FwResultCode resultCode) {
        return build(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败（自定义状态码 + 消息）
     */
    public static <T> FwResult<T> fail(Integer code, String msg) {
        return build(code, msg, null);
    }

    // ============================ 内部构建方法 ============================

    private static <T> FwResult<T> build(Integer code, String msg, T data) {
        FwResult<T> result = new FwResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
