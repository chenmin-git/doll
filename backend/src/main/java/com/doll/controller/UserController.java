package com.doll.controller;

import com.doll.common.Result;
import com.doll.dto.LoginResponse;
import com.doll.entity.User;
import com.doll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody User user) {
        LoginResponse response = userService.login(user.getUsername(), user.getPassword());
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registered = userService.register(user);
        return Result.success(registered);
    }

    @GetMapping("/list")
    public Result<List<User>> listUsers() {
        return Result.success(userService.list());
    }

    @PutMapping("/profile/{id}")
    public Result<User> updateProfile(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateProfile(id, user);
        return Result.success(updated);
    }

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping("/password/{id}")
    public Result<?> changePassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> passwords) {
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}
