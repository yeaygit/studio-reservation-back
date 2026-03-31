package com.toy.project.studio.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.toy.project.studio.config.jwt.JwtUtil;

class AuthTokenResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void fromConvertsAccessTokenExpirationToKoreaTime() {
        AuthTokens authTokens = AuthTokens.of(
                new JwtUtil.TokenSubject(1L, "ylkim", List.of("ROLE_ADMIN")),
                new JwtUtil.AccessToken("access-token", Instant.parse("2026-03-27T07:35:08.224514300Z")),
                new JwtUtil.RefreshToken("refresh-token", "jti", Instant.parse("2026-04-10T07:05:08.224514300Z"))
        );

        AuthTokenResponse response = AuthTokenResponse.from(authTokens);

        assertThat(response.accessTokenExpiresAt())
                .isEqualTo(Instant.parse("2026-03-27T07:35:08.224514300Z").atOffset(ZoneOffset.ofHours(9)));
    }

    @Test
    void serializesAccessTokenExpirationWithKoreaOffset() throws Exception {
        AuthTokenResponse response = new AuthTokenResponse(
                "access-token",
                Instant.parse("2026-03-27T07:35:08.224514300Z")
                        .atZone(java.time.ZoneId.of("Asia/Seoul"))
                        .toOffsetDateTime(),
                1L,
                "ylkim",
                List.of("ROLE_ADMIN")
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"accessTokenExpiresAt\":\"2026-03-27T16:35:08.2245143+09:00\"");
    }
}
