package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.User;
import com.parking.mapper.UserMapper;
import com.parking.service.UserService;
import com.parking.dto.LoginRequest;
import com.parking.dto.LoginResponse;
import com.parking.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", loginRequest.getUsername()));
        if (user == null) {
            return new LoginResponse(false, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getUserType());
    }

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (userMapper.selectOne(new QueryWrapper<User>().eq("phone", user.getPhone())) != null) {
            throw new RuntimeException("手机号已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserType(0); // 默认为普通用户
        user.setStatus(1); // 默认启用

        save(user);
        return user;
    }

    @Override
    public boolean checkUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username)) == null;
    }

    @Override
    public boolean checkPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone)) == null;
    }
}