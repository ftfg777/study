package com.example.study.jwt;

import com.example.study.exception.CommonExceptionHandler;
import com.example.study.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(RefreshTokenMapper refreshTokenMapper, JwtUtil jwtUtil) {
        this.refreshTokenMapper = refreshTokenMapper;
        this.jwtUtil = jwtUtil;
    }

    public String reissueAccessToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new CommonExceptionHandler(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.extractEmail(refreshToken);

        RefreshToken savedToken = refreshTokenMapper.findByEmail(email);

        if (savedToken == null || !savedToken.getToken().equals(refreshToken)) {
            throw new CommonExceptionHandler(ErrorCode.MISMATCHED_REFRESH_TOKEN);
        }

        return jwtUtil.generateAccessToken(email);
    }

}
