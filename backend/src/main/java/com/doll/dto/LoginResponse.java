package com.doll.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private Integer role;
    
    public LoginResponse(String token, Long userId, Integer role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }
}
