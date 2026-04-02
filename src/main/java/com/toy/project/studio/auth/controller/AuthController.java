package com.toy.project.studio.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.auth.dto.request.LoginRequest;
import com.toy.project.studio.auth.dto.response.AuthSessionResponse;
import com.toy.project.studio.auth.dto.response.AuthTokenResponse;
import com.toy.project.studio.auth.dto.response.AuthTokens;
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

    @GetMapping("/session")
    public ResponseEntity<AuthSessionResponse> session(
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String refreshToken
    ) {
        // SPA가 토큰 복구를 시도할 수 있는 상태인지 먼저 확인할 때 사용한다.
        boolean authenticated = authService.hasValidSession(refreshToken);

        if (!authenticated && StringUtils.hasText(refreshToken)) {
            // 이미 무효한 refresh cookie라면 브라우저에서도 바로 정리한다.
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookieProvider.delete().toString())
                    .body(AuthSessionResponse.of(false));
        }

        return ResponseEntity.ok(AuthSessionResponse.of(authenticated));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        // access token은 body로, refresh token은 HttpOnly cookie로 내려준다.
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
        // refresh token은 cookie에서만 받아서 프론트가 직접 다루지 않도록 한다.
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
        // 서버의 refresh 세션을 지우고 브라우저 cookie도 함께 만료시킨다.
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieProvider.delete().toString())
                .build();
    }
}
