package com.toy.project.studio.auth.support;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieProvider {

    public static final String COOKIE_NAME = "refreshToken";

    private final boolean secure;
    private final String sameSite;
    private final String path;

    public RefreshTokenCookieProvider(
            @Value("${app.auth.refresh-cookie.secure:true}") boolean secure,
            @Value("${app.auth.refresh-cookie.same-site:Strict}") String sameSite,
            @Value("${app.auth.refresh-cookie.path:/auth}") String path
    ) {
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
    }

    public ResponseCookie create(String refreshToken, Instant expiresAt) {
        Duration maxAge = Duration.between(Instant.now(), expiresAt);
        if (maxAge.isNegative()) {
            maxAge = Duration.ZERO;
        }

        // HttpOnly: JS에서 읽지 못하게 막음
        // Secure: HTTPS에서만 전송
        // SameSite: CSRF 위험을 줄이기 위한 기본 설정
        return ResponseCookie.from(COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie delete() {
        // 같은 이름/경로의 쿠키를 maxAge 0으로 내려서 브라우저에서 삭제시킵니다.
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(Duration.ZERO)
                .build();
    }
}
