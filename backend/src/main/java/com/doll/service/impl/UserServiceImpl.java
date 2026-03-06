package com.doll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.dto.LoginResponse;
import com.doll.entity.User;
import com.doll.mapper.UserMapper;
import com.doll.service.UserService;
import com.doll.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public LoginResponse login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).eq(User::getPassword, password);
        User user = getOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = JwtUtil.generateToken(user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getRole());
    }

    @Override
    public User register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setStatus(1);
        save(user);
        return user;
    }

    @Override
    public User updateProfile(Long userId, User user) {
        user.setId(userId);
        updateById(user);
        return getById(userId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(newPassword);
        updateById(user);
    }
}
