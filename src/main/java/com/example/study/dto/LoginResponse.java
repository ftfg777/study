package com.example.study.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private String email;
    private String name;

    private String token;

    public LoginResponse(String email, String name, String token) {
        this.email = email;
        this.name = name;
        this.token = token;
    }
}
