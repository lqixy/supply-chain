package cn.lqixy.common.framework.vo;

import cn.lqixy.common.framework.exception.GlobalException;
import cn.lqixy.common.framework.exception.ServiceException;
import cn.lqixy.common.framework.exception.enums.GlobalErrorCodeConstants;

import java.io.Serializable;

/**
 * @param <T>
 */
public final class CommonResult<T> implements Serializable {

    private Integer code;

    private T data;

    private String message;

    private String detailMessage;


    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     *
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T> 返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMessage(), result.detailMessage);
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        return error(code, message, null);
    }

    public static <T> CommonResult<T> error(Integer code, String message, String detailMessage) {
//        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.message = message;
        result.detailMessage = detailMessage;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.message = "";
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public CommonResult<T> setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

//    @JSONField(serialize = false) // 避免序列化
    public boolean isSuccess() {
        return GlobalErrorCodeConstants.SUCCESS.getCode().equals(code);
    }

//    @JSONField(serialize = false) // 避免序列化
    public boolean isError() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", detailMessage='" + detailMessage + '\'' +
                '}';
    }

    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link GlobalException} 或 {@link ServiceException} 异常
     */
    public void checkError() throws GlobalException, ServiceException {
        if (isSuccess()) {
            return;
        }
        // 全局异常
        if (GlobalErrorCodeConstants.isMatch(code)) {
            throw new GlobalException(code, message).setDetailMessage(detailMessage);
        }
        // 业务异常
        throw new ServiceException(code, message).setDetailMessage(detailMessage);
    }

    public static <T> CommonResult<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage(),
                serviceException.getDetailMessage());
    }

    public static <T> CommonResult<T> error(GlobalException globalException) {
        return error(globalException.getCode(), globalException.getMessage(),
                globalException.getDetailMessage());
    }
}
