package com.toy.project.studio.config.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // access/refresh 모두 공통으로 넣는 핵심 claim
    private static final String USER_ID_CLAIM = "userId";
    private static final String USERNAME_CLAIM = "username";
    private static final String ROLES_CLAIM = "roles";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private final String issuer;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;
    private final SecretKey signingKey;

    public JwtUtil(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") Duration accessTokenExpiration,
            @Value("${app.jwt.refresh-token-expiration}") Duration refreshTokenExpiration
    ) {
        Assert.hasText(issuer, "app.jwt.issuer must not be blank");
        Assert.hasText(secret, "app.jwt.secret must not be blank");
        Assert.notNull(accessTokenExpiration, "app.jwt.access-token-expiration must not be null");
        Assert.notNull(refreshTokenExpiration, "app.jwt.refresh-token-expiration must not be null");

        this.issuer = issuer;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public AccessToken generateAccessToken(TokenSubject subject) {
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plus(accessTokenExpiration);

        // access token은 매 요청 인증에 사용되므로 tokenType=ACCESS로 고정합니다.
        String accessToken = Jwts.builder()
                .issuer(issuer)
                .subject(subject.username())
                .claim(USER_ID_CLAIM, subject.userId())
                .claim(USERNAME_CLAIM, subject.username())
                .claim(ROLES_CLAIM, subject.roles())
                .claim(TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessTokenExpiresAt))
                .signWith(signingKey)
                .compact();

        return new AccessToken(accessToken, accessTokenExpiresAt);
    }

    public RefreshToken generateRefreshToken(TokenSubject subject) {
        Instant now = Instant.now();
        Instant refreshTokenExpiresAt = now.plus(refreshTokenExpiration);
        // rotation 추적을 위해 refresh token마다 고유 jti를 부여합니다.
        String jti = UUID.randomUUID().toString();

        // refresh token은 재발급 API에서만 사용되도록 tokenType=REFRESH를 넣습니다.
        String refreshToken = Jwts.builder()
                .issuer(issuer)
                .subject(subject.username())
                .id(jti)
                .claim(USER_ID_CLAIM, subject.userId())
                .claim(USERNAME_CLAIM, subject.username())
                .claim(ROLES_CLAIM, subject.roles())
                .claim(TOKEN_TYPE_CLAIM, TokenType.REFRESH.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshTokenExpiresAt))
                .signWith(signingKey)
                .compact();

        return new RefreshToken(refreshToken, jti, refreshTokenExpiresAt);
    }

    public AccessTokenClaims parseAccessToken(String token) {
        // access token 전용 파서입니다. refresh token이 들어오면 타입 오류를 냅니다.
        Claims claims = parseClaims(token, TokenType.ACCESS);
        return new AccessTokenClaims(
                extractLongClaim(claims, USER_ID_CLAIM, ErrorCode.INVALID_ACCESS_TOKEN),
                extractTextClaim(claims, USERNAME_CLAIM, ErrorCode.INVALID_ACCESS_TOKEN),
                extractRolesClaim(claims, ErrorCode.INVALID_ACCESS_TOKEN),
                extractExpiration(claims, ErrorCode.INVALID_ACCESS_TOKEN)
        );
    }

    public RefreshTokenClaims parseRefreshToken(String token) {
        // refresh token 전용 파서입니다. access token이 들어오면 타입 오류를 냅니다.
        Claims claims = parseClaims(token, TokenType.REFRESH);
        return new RefreshTokenClaims(
                extractLongClaim(claims, USER_ID_CLAIM, ErrorCode.INVALID_REFRESH_TOKEN),
                extractTextClaim(claims, USERNAME_CLAIM, ErrorCode.INVALID_REFRESH_TOKEN),
                extractRolesClaim(claims, ErrorCode.INVALID_REFRESH_TOKEN),
                extractTextClaim(claims, Claims.ID, ErrorCode.INVALID_REFRESH_TOKEN),
                extractExpiration(claims, ErrorCode.INVALID_REFRESH_TOKEN)
        );
    }

    public Authentication createAuthentication(AccessTokenClaims tokenClaims) {
        // 필터에서 사용할 Security principal 객체로 변환합니다.
        JwtPrincipal principal = new JwtPrincipal(
                tokenClaims.userId(),
                tokenClaims.username(),
                tokenClaims.roles()
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                tokenClaims.roles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    private Claims parseClaims(String token, TokenType expectedTokenType) {
        try {
            // 공통 검증: 서명 + issuer + 만료시간
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // access 자리에는 access만, refresh 자리에는 refresh만 오게 강제합니다.
            String tokenType = extractTextClaim(
                    claims,
                    TOKEN_TYPE_CLAIM,
                    expectedTokenType == TokenType.ACCESS ? ErrorCode.INVALID_ACCESS_TOKEN : ErrorCode.INVALID_REFRESH_TOKEN
            );

            if (!expectedTokenType.name().equals(tokenType)) {
                throw new CustomException(
                        expectedTokenType == TokenType.ACCESS
                                ? ErrorCode.INVALID_ACCESS_TOKEN_TYPE
                                : ErrorCode.INVALID_REFRESH_TOKEN_TYPE
                );
            }

            return claims;
        } catch (ExpiredJwtException exception) {
            throw new CustomException(
                    expectedTokenType == TokenType.ACCESS
                            ? ErrorCode.EXPIRED_ACCESS_TOKEN
                            : ErrorCode.EXPIRED_REFRESH_TOKEN
            );
        } catch (CustomException exception) {
            throw exception;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new CustomException(
                    expectedTokenType == TokenType.ACCESS
                            ? ErrorCode.INVALID_ACCESS_TOKEN
                            : ErrorCode.INVALID_REFRESH_TOKEN
            );
        }
    }

    private Long extractLongClaim(Claims claims, String claimName, ErrorCode errorCode) {
        Object claimValue = claims.get(claimName);
        if (claimValue instanceof Number number) {
            return number.longValue();
        }

        throw new CustomException(errorCode);
    }

    private String extractTextClaim(Claims claims, String claimName, ErrorCode errorCode) {
        String claimValue;

        if (Claims.ID.equals(claimName)) {
            claimValue = claims.getId();
        } else {
            claimValue = claims.get(claimName, String.class);
        }

        if (StringUtils.hasText(claimValue)) {
            return claimValue;
        }

        throw new CustomException(errorCode);
    }

    private List<String> extractRolesClaim(Claims claims, ErrorCode errorCode) {
        Object claimValue = claims.get(ROLES_CLAIM);
        if (!(claimValue instanceof List<?> rawRoles) || rawRoles.isEmpty()) {
            throw new CustomException(errorCode);
        }

        return rawRoles.stream()
                .map(role -> {
                    if (role instanceof String roleName && StringUtils.hasText(roleName)) {
                        return roleName;
                    }

                    throw new CustomException(errorCode);
                })
                .toList();
    }

    private Instant extractExpiration(Claims claims, ErrorCode errorCode) {
        Date expiration = claims.getExpiration();
        if (expiration == null) {
            throw new CustomException(errorCode);
        }

        return expiration.toInstant();
    }

    public record TokenSubject(
            Long userId,
            String username,
            List<String> roles
    ) {
        public TokenSubject {
            roles = List.copyOf(roles);
        }
    }

    public record AccessToken(
            String token,
            Instant expiresAt
    ) {
    }

    public record RefreshToken(
            String token,
            String jti,
            Instant expiresAt
    ) {
    }

    public record AccessTokenClaims(
            Long userId,
            String username,
            List<String> roles,
            Instant expiresAt
    ) {
        public AccessTokenClaims {
            roles = List.copyOf(roles);
        }
    }

    public record RefreshTokenClaims(
            Long userId,
            String username,
            List<String> roles,
            String jti,
            Instant expiresAt
    ) {
        public RefreshTokenClaims {
            roles = List.copyOf(roles);
        }
    }
}
