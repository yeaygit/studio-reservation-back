package com.toy.project.studio.auth.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public record AuthTokenResponse(
        String accessToken,
        OffsetDateTime accessTokenExpiresAt,
        Long userId,
        String username,
        List<String> roles
) {

    private static final ZoneId KOREA_TIME_ZONE = ZoneId.of("Asia/Seoul");

    public AuthTokenResponse {
        roles = List.copyOf(roles);
    }

    public static AuthTokenResponse from(AuthTokens authTokens) {
        return new AuthTokenResponse(
                authTokens.accessToken(),
                authTokens.accessTokenExpiresAt().atZone(KOREA_TIME_ZONE).toOffsetDateTime(),
                authTokens.userId(),
                authTokens.username(),
                authTokens.roles()
        );
    }
}
