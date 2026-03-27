package com.toy.project.studio.auth.dto;

import java.time.Instant;
import java.util.List;

import com.toy.project.studio.config.jwt.JwtUtil.AccessToken;
import com.toy.project.studio.config.jwt.JwtUtil.RefreshToken;
import com.toy.project.studio.config.jwt.JwtUtil.TokenSubject;

public record AuthTokens(
        Long userId,
        String username,
        List<String> roles,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {

    public AuthTokens {
        roles = List.copyOf(roles);
    }

    public static AuthTokens of(TokenSubject subject, AccessToken accessToken, RefreshToken refreshToken) {
        return new AuthTokens(
                subject.userId(),
                subject.username(),
                subject.roles(),
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.token(),
                refreshToken.expiresAt()
        );
    }
}
