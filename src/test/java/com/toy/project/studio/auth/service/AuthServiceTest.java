package com.toy.project.studio.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.toy.project.studio.auth.repository.AdminRepository;
import com.toy.project.studio.config.jwt.JwtUtil;
import com.toy.project.studio.config.jwt.JwtUtil.RefreshTokenClaims;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void hasValidSessionReturnsFalseWhenRefreshTokenIsMissing() {
        assertThat(authService.hasValidSession(null)).isFalse();

        verifyNoInteractions(jwtUtil, refreshTokenService);
    }

    @Test
    void hasValidSessionReturnsFalseWhenRefreshTokenIsInvalid() {
        when(jwtUtil.parseRefreshToken("invalid-refresh"))
                .thenThrow(new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        assertThat(authService.hasValidSession("  invalid-refresh  ")).isFalse();

        verify(jwtUtil).parseRefreshToken("invalid-refresh");
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void hasValidSessionReturnsFalseWhenRefreshTokenCannotBeRecovered() {
        RefreshTokenClaims claims = new RefreshTokenClaims(
                1L,
                "admin",
                List.of("ROLE_ADMIN"),
                "refresh-jti",
                Instant.parse("2026-04-01T00:00:00Z")
        );

        when(jwtUtil.parseRefreshToken("refresh-token")).thenReturn(claims);
        doThrow(new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND_IN_STORE))
                .when(refreshTokenService)
                .validateRefreshToken("refresh-token", claims, "web");

        assertThat(authService.hasValidSession("refresh-token")).isFalse();
    }

    @Test
    void hasValidSessionReturnsTrueWhenRefreshTokenCanBeUsedToRefresh() {
        RefreshTokenClaims claims = new RefreshTokenClaims(
                1L,
                "admin",
                List.of("ROLE_ADMIN"),
                "refresh-jti",
                Instant.parse("2026-04-01T00:00:00Z")
        );

        when(jwtUtil.parseRefreshToken("refresh-token")).thenReturn(claims);

        assertThat(authService.hasValidSession("refresh-token")).isTrue();

        verify(refreshTokenService).validateRefreshToken("refresh-token", claims, "web");
    }
}
