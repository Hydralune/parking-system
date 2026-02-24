package com.parking.dto;

import lombok.Data;

@Data
public class ResponseResult<T> {
    private int code;
    private String message;
    private boolean success;
    private T data;

    public ResponseResult() {}

    public ResponseResult(int code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public ResponseResult(int code, String message, boolean success, T data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", true, data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(200, message, true, data);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(500, message, false, null);
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<>(code, message, false, null);
    }

    public static <T> ResponseResult<T> custom(int code, String message, boolean success, T data) {
        return new ResponseResult<>(code, message, success, data);
    }
}