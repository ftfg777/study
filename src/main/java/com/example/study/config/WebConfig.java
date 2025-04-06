package com.example.study.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // 프론트엔드 주소
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Refresh-Token") // 👈 클라이언트가 이 헤더 읽을 수 있도록 허용
            .allowCredentials(true); // 쿠키도 허용 (Refresh Token용)
    }
}
