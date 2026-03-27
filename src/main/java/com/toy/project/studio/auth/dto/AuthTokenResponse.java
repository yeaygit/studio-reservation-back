package com.toy.project.studio.auth.dto;

import java.time.Instant;
import java.util.List;

public record AuthTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        Long userId,
        String username,
        List<String> roles
) {

    public AuthTokenResponse {
        roles = List.copyOf(roles);
    }

    public static AuthTokenResponse from(AuthTokens authTokens) {
        return new AuthTokenResponse(
                authTokens.accessToken(),
                authTokens.accessTokenExpiresAt(),
                authTokens.userId(),
                authTokens.username(),
                authTokens.roles()
        );
    }
}
