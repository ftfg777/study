package com.example.study.controller;

import com.example.study.dto.ApiResponse;
import com.example.study.dto.LoginRequest;
import com.example.study.dto.LoginResponse;
import com.example.study.dto.RefreshTokenRequest;
import com.example.study.dto.RegisterRequest;
import com.example.study.dto.TokenResponse;
import com.example.study.exception.ErrorCode;
import com.example.study.jwt.JwtUtil;
import com.example.study.jwt.RefreshToken;
import com.example.study.jwt.RefreshTokenMapper;
import com.example.study.mapper.UserMapper;
import com.example.study.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserMapper userMapper, RefreshTokenMapper refreshTokenMapper,
        JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterRequest registerRequest) {
        User user = User.builder()
            .email(registerRequest.getEmail())
            .name(registerRequest.getName())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role("ROLE_USER").build();

        userMapper.insertUser(user);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        User foundUser = userMapper.emailLogin(loginRequest.getEmail());

        if (foundUser != null && passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            String accessToken = jwtUtil.generateAccessToken(foundUser.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(foundUser.getEmail());

            refreshTokenMapper.save(new RefreshToken(foundUser.getEmail(), refreshToken, jwtUtil.getRefreshTokenExpiry()));

            return ResponseEntity.ok(ApiResponse.success(
                new LoginResponse(
                foundUser.getEmail(),
                foundUser.getName(),
                accessToken, refreshToken))
            );
        }

        return ResponseEntity
            .status(ErrorCode.INVALID_CREDENTIALS.getStatus())
            .body(ApiResponse.error(ErrorCode.INVALID_CREDENTIALS));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        try {
            String email = jwtUtil.extractEmailFromRefreshToken(request.getRefreshToken());

            String newAccessToken = jwtUtil.generateAccessToken(email);
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

            return ResponseEntity.ok(ApiResponse.success(new TokenResponse(newAccessToken, newRefreshToken)));

        }catch (ExpiredJwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.EXPIRED_REFRESH_TOKEN));

        }catch (JwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }
    }
}


