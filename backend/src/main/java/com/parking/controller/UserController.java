package com.parking.controller;

import com.parking.dto.LoginRequest;
import com.parking.dto.ResponseResult;
import com.parking.entity.User;
import com.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseResult<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseResult.success(userService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseResult<?> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseResult.success("注册成功", registeredUser);
        } catch (RuntimeException e) {
            return ResponseResult.error(e.getMessage());
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseResult<Boolean> checkUsername(@PathVariable String username) {
        boolean available = userService.checkUsername(username);
        return ResponseResult.success(available);
    }

    @GetMapping("/check-phone/{phone}")
    public ResponseResult<Boolean> checkPhone(@PathVariable String phone) {
        boolean available = userService.checkPhone(phone);
        return ResponseResult.success(available);
    }
}