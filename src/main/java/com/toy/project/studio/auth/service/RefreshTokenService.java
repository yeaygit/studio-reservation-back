package com.toy.project.studio.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.toy.project.studio.config.jwt.JwtUtil.RefreshTokenClaims;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@Service
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";
    private static final String HASH_FIELD = "refreshTokenHash";
    private static final String JTI_FIELD = "jti";
    private static final String EXPIRES_AT_FIELD = "expiresAt";

    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;

    public RefreshTokenService(StringRedisTemplate stringRedisTemplate, PasswordEncoder passwordEncoder) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveRefreshToken(
            Long userId,
            String deviceId,
            String refreshToken,
            String jti,
            Instant expiresAt
    ) {
        String key = buildKey(userId, deviceId);

        stringRedisTemplate.opsForHash().putAll(key, Map.of(
                HASH_FIELD, passwordEncoder.encode(refreshToken),
                JTI_FIELD, jti,
                EXPIRES_AT_FIELD, expiresAt.toString()
        ));

        stringRedisTemplate.expire(key, calculateTtl(expiresAt));
    }

    public void validateRefreshToken(String refreshToken, RefreshTokenClaims claims, String deviceId) {
        StoredRefreshToken storedRefreshToken = findRefreshToken(claims.userId(), deviceId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND_IN_STORE));

        if (storedRefreshToken.expiresAt().isBefore(Instant.now())) {
            deleteRefreshToken(claims.userId(), deviceId);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        boolean tokenMatches = passwordEncoder.matches(refreshToken, storedRefreshToken.refreshTokenHash());
        boolean jtiMatches = storedRefreshToken.jti().equals(claims.jti());

        if (tokenMatches && jtiMatches) {
            return;
        }

        deleteRefreshToken(claims.userId(), deviceId);
        throw new CustomException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
    }

    public void rotateRefreshToken(
            Long userId,
            String deviceId,
            String newRefreshToken,
            String newJti,
            Instant newExpiresAt
    ) {
        saveRefreshToken(userId, deviceId, newRefreshToken, newJti, newExpiresAt);
    }

    public void deleteRefreshToken(Long userId, String deviceId) {
        stringRedisTemplate.delete(buildKey(userId, deviceId));
    }

    private Optional<StoredRefreshToken> findRefreshToken(Long userId, String deviceId) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(buildKey(userId, deviceId));
        if (entries.isEmpty()) {
            return Optional.empty();
        }

        Object refreshTokenHash = entries.get(HASH_FIELD);
        Object jti = entries.get(JTI_FIELD);
        Object expiresAt = entries.get(EXPIRES_AT_FIELD);

        if (!(refreshTokenHash instanceof String refreshTokenHashValue)
                || !(jti instanceof String jtiValue)
                || !(expiresAt instanceof String expiresAtValue)) {
            return Optional.empty();
        }

        return Optional.of(new StoredRefreshToken(
                refreshTokenHashValue,
                jtiValue,
                Instant.parse(expiresAtValue)
        ));
    }

    private Duration calculateTtl(Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        return ttl.isNegative() ? Duration.ZERO : ttl;
    }

    private String buildKey(Long userId, String deviceId) {
        return KEY_PREFIX + userId + ":" + deviceId;
    }

    private record StoredRefreshToken(
            String refreshTokenHash,
            String jti,
            Instant expiresAt
    ) {
    }
}