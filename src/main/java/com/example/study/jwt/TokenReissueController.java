package com.example.study.jwt;

import com.example.study.dto.ApiResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenReissueController {

    private final RefreshTokenService refreshTokenService;

    public TokenReissueController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Map<String,String>>> reissueAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken = refreshTokenService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(ApiResponse.success(Map.of("accessToken", newAccessToken)));
    }
}
