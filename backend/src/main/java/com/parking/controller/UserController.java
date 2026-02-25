package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.dto.LoginRequest;
import com.parking.dto.ResponseResult;
import com.parking.entity.User;
import com.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // 初始化测试用户（仅开发环境使用）
    @PostMapping("/init-users")
    public ResponseResult<?> initUsers() {
        String encodedPwd = passwordEncoder.encode("123456");
        Object[][] users = {
            {"admin",     "13800000001", 1},
            {"kkk",       "13800000012", 0},
            {"zhang_wei", "13800000002", 0},
            {"li_fang",   "13800000003", 0},
            {"wang_jun",  "13800000004", 0},
            {"chen_xia",  "13800000005", 0},
            {"liu_yang",  "13800000006", 0},
            {"zhao_lei",  "13800000007", 0},
            {"sun_min",   "13800000008", 0},
            {"zhou_tao",  "13800000009", 0},
            {"wu_jing",   "13800000010", 0},
            {"zheng_hao", "13800000011", 0},
        };
        for (Object[] row : users) {
            String username = (String) row[0];
            if (userService.getOne(new QueryWrapper<User>().eq("username", username)) != null) continue;
            User u = new User();
            u.setUsername(username);
            u.setPassword(encodedPwd);
            u.setPhone((String) row[1]);
            u.setEmail(username + "@example.com");
            u.setUserType((Integer) row[2]);
            u.setStatus(1);
            userService.save(u);
        }
        return ResponseResult.success("初始化完成，所有用户密码为 123456");
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