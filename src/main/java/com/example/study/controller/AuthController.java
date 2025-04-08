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
        String refreshToken = request.getRefreshToken();

        log.info("🔄 재발급 요청 받은 리프레시 토큰: {}", refreshToken);

        // 리프레시 토큰 유효성 검사
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            log.warn("❌ 유효하지 않은 리프레시 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        // 이메일 추출
        String email = jwtUtil.extractEmailFromRefreshToken(refreshToken);
        log.info("✅ 토큰에서 추출한 사용자 이메일: {}", email);

        // DB에 저장된 리프레시 토큰 가져오기
        RefreshToken savedToken = refreshTokenMapper.findByEmail(email);
        log.info("✅ 이메일로 추출한 토큰: {}", savedToken.getToken());

        if (savedToken.getToken() == null || !savedToken.getToken().equals(refreshToken)) {
            log.warn("❌ 저장된 토큰과 일치하지 않거나 존재하지 않음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ErrorCode.MISMATCHED_REFRESH_TOKEN));
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        Timestamp newExpiry = jwtUtil.getRefreshTokenExpiry();

        // 저장된 리프레시 토큰 갱신
        refreshTokenMapper.update(new RefreshToken(email, newRefreshToken, newExpiry));
        log.info("🔐 새로운 액세스 토큰 발급 완료");
        log.info("🆕 새로운 리프레시 토큰 저장 완료");

        // 응답
        return ResponseEntity.ok
            (ApiResponse.success(new TokenResponse(newAccessToken, newRefreshToken))
        );
    }
}


