package com.doll.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doll.dto.LoginResponse;
import com.doll.entity.User;

public interface UserService extends IService<User> {
    LoginResponse login(String username, String password);

    User register(User user);

    User updateProfile(Long userId, User user);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
