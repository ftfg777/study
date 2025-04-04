package com.example.study.controller;

import com.example.study.dto.ApiResponse;
import com.example.study.dto.LoginRequest;
import com.example.study.dto.LoginResponse;
import com.example.study.dto.RegisterRequest;
import com.example.study.exception.ErrorCode;
import com.example.study.jwt.JwtUtil;
import com.example.study.mapper.UserMapper;
import com.example.study.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
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
            String token = jwtUtil.generateToken(foundUser.getEmail());

            return ResponseEntity.ok(ApiResponse.success(
                new LoginResponse(
                foundUser.getEmail(),
                foundUser.getName(),
                token))
            );
        }
        return ResponseEntity
            .status(ErrorCode.INVALID_CREDENTIALS.getStatus())
            .body(ApiResponse.error(ErrorCode.INVALID_CREDENTIALS));
    }
}


