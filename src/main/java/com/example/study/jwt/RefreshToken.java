package com.example.study.jwt;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefreshToken {

    private String email;
    private String token;
    private Timestamp expiryAt;

    public RefreshToken(String email, String token, Timestamp expiryAt) {
        this.email = email;
        this.token = token;
        this.expiryAt = expiryAt;
    }
}
