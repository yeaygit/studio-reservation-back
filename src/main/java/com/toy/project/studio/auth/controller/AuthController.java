package com.toy.project.studio.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.auth.dto.AuthTokenResponse;
import com.toy.project.studio.auth.dto.AuthTokens;
import com.toy.project.studio.auth.dto.LoginRequest;
import com.toy.project.studio.auth.service.AuthService;
import com.toy.project.studio.auth.support.RefreshTokenCookieProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        // 로그인 성공 시 access token은 body로, refresh token은 HttpOnly 쿠키로 내려줍니다.
        AuthTokens authTokens = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        refreshTokenCookieProvider.create(authTokens.refreshToken(), authTokens.refreshTokenExpiresAt()).toString())
                .body(AuthTokenResponse.from(authTokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String refreshToken
    ) {
        // refresh token은 오직 쿠키에서만 읽습니다. body나 Authorization 헤더에서는 받지 않습니다.
        AuthTokens authTokens = authService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        refreshTokenCookieProvider.create(authTokens.refreshToken(), authTokens.refreshTokenExpiresAt()).toString())
                .body(AuthTokenResponse.from(authTokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String refreshToken
    ) {
        // 로그아웃은 Redis의 refresh 세션을 지우고 쿠키도 비웁니다.
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieProvider.delete().toString())
                .build();
    }
}
