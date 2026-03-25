package com.xinyi.androidbasic.model;

/**
 * 数据请求的响应体封装
 *
 * @author 新一
 * @date 2025/3/19 9:03
 */
public class ResponseData<T> {

    /**
     * 响应码
     */
    private int code = 0;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success = true;

    public ResponseData() { }

    public ResponseData(int code, boolean success, T data, String message) {
        this.code = code;
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
