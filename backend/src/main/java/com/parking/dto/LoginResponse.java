package com.parking.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private Integer userType;
    private String message;
    private Boolean success;

    public LoginResponse(String token, Long userId, String username, Integer userType) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.userType = userType;
        this.success = true;
        this.message = "登录成功";
    }

    public LoginResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}