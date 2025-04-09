package com.example.study.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private static final String ACCESS_SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private static final String REFRESH_SECRET_KEY = "your-256-bit-refresh-secret-your-256-bit-refresh-secret";
    private static final Key ACCESS_KEY = Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes());
    private static final Key REFRESH_KEY = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());


    // 공통 토큰 생성 (공통화)
    private String generateToken(String email, Key signingKey, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(signingKey)
            .compact();
    }


    // 액세스 토큰 생성
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_KEY, jwtProperties.getAccessTokenExpiration());
    }


    // 리프레시 토큰 생성
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_KEY, jwtProperties.getRefreshTokenExpiration());
    }


    // 토큰에서 이메일 추출 (공통화)
    private String extractEmail(String token, Key key) throws JwtException {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
    }

    public String extractEmailFromAccessToken(String token) {
        return extractEmail(token, ACCESS_KEY);
    }

    public String extractEmailFromRefreshToken(String token) {
        return extractEmail(token, REFRESH_KEY);
    }


    // 액세스 토큰 유효성 검증
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        String email = extractEmailFromAccessToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token, ACCESS_KEY));
    }

    // 리프레시 토큰 유효성 검증
    public void validateRefreshToken(String token) throws JwtException{
            Jwts.parserBuilder()
                .setSigningKey(REFRESH_KEY)
                .build()
                .parseClaimsJws(token);
    }

    private boolean isTokenExpired(String token, Key key) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true; // 만료 또는 유효하지 않음
        }
    }

    // DB용 만료 시간 계산
    public Timestamp getRefreshTokenExpiry() {
        return new Timestamp(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration());
    }

}
