package com.example.study.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String email, String name, String accessToken, String refreshToken) {
        this.email = email;
        this.name = name;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
