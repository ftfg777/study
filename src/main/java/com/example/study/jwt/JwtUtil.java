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

    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private static final String REFRESH_SECRET_KEY = "your-256-bit-refresh-secret-your-256-bit-refresh-secret";

    private static final Key accesskey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final Key refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());


    // 액세스 토큰 생성
    public String generateAccessToken(String email) {
        return Jwts.builder()
            .setSubject(email)  // username 대신 email 사용
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
            .signWith(accesskey)
            .compact();
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(String email){
        long expirationMillis = jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000L; // 7일 -> 밀리초
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMillis);

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(refreshKey)
            .compact();
    }

    // 토큰에서 이메일 추출 (액세스 토큰)
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(accesskey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();

        }catch (JwtException ex){
            return null;
        }
    }

    public String extractEmailFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // 토큰 유효성 검증 (액세스 토큰)
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(accesskey)
                .build()
                .parseClaimsJws(token); // 토큰이 유효한지 확인
            return true;
        }catch (JwtException ex){
            return false;
        }
    }

    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token);
            return true;
        }catch (JwtException ex){
            return false;
        }
    }


    // DB에 저장 변환 메소드 필요
    public Timestamp getRefreshTokenExpiry() {
        return Timestamp.valueOf(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()));
    }

}
