package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.User;
import com.parking.dto.LoginRequest;
import com.parking.dto.LoginResponse;

public interface UserService extends IService<User> {
    LoginResponse login(LoginRequest loginRequest);
    User register(User user);
    boolean checkUsername(String username);
    boolean checkPhone(String phone);
}