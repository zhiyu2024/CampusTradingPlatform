package cn.gdsdxy.campustrading.common.exception;

import cn.gdsdxy.campustrading.common.result.FwResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 只要代码里抛出了异常，都会被这里捕获
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义业务异常 (优先级最高)
     * 例如：throw new BusinessException(FwResultCode.USER_LOGIN_ERROR)
     */
    @ExceptionHandler(BusinessException.class)
    public FwResult<Object> handleBusinessException(BusinessException e) {
        // 打印错误日志到控制台，方便后端排查
        e.printStackTrace();

        // 返回自定义的错误码和错误信息
        return FwResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 捕获所有 RuntimeException (运行时异常)
     * 例如：NullPointerException, IndexOutOfBoundsException
     */
    @ExceptionHandler(RuntimeException.class)
    public FwResult<Object> handleRuntimeException(RuntimeException e) {
        // 打印错误日志到控制台，方便后端排查
        e.printStackTrace();

        // 告诉前端：操作失败，并把错误信息发过去
        return FwResult.fail(e.getMessage());
    }

    /**
     * 捕获所有 Exception (最大的兜底)
     */
    @ExceptionHandler(Exception.class)
    public FwResult<Object> handleException(Exception e) {
        e.printStackTrace();
        return FwResult.fail(500, "系统繁忙，请联系管理员");
    }
}