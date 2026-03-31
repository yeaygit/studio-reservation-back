package com.toy.project.studio.auth.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.toy.project.studio.auth.dto.request.LoginRequest;
import com.toy.project.studio.auth.dto.response.AuthTokens;
import com.toy.project.studio.auth.entity.Admin;
import com.toy.project.studio.auth.repository.AdminRepository;
import com.toy.project.studio.config.jwt.JwtUtil;
import com.toy.project.studio.config.jwt.JwtUtil.AccessToken;
import com.toy.project.studio.config.jwt.JwtUtil.RefreshToken;
import com.toy.project.studio.config.jwt.JwtUtil.RefreshTokenClaims;
import com.toy.project.studio.config.jwt.JwtUtil.TokenSubject;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String DEFAULT_DEVICE_ID = "web";
    private static final List<String> ADMIN_ROLES = List.of("ROLE_ADMIN");

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthTokens login(LoginRequest request) {
        // 로그인에 성공하면 두 토큰을 발급하고 refresh token은 저장소에 보관한다.
        String username = request.username().trim();
        String password = request.password().trim();

        Admin admin = adminRepository.findAdminByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        TokenSubject subject = new TokenSubject(admin.getId(), admin.getUsername(), ADMIN_ROLES);
        AccessToken accessToken = jwtUtil.generateAccessToken(subject);
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(subject);

        refreshTokenService.saveRefreshToken(
                subject.userId(),
                DEFAULT_DEVICE_ID,
                refreshToken.token(),
                refreshToken.jti(),
                refreshToken.expiresAt()
        );

        return AuthTokens.of(subject, accessToken, refreshToken);
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        // JWT 자체와 저장된 refresh 세션이 모두 유효할 때만 재발급한다.
        RefreshTokenClaims claims = validateRefreshToken(refreshToken);

        TokenSubject subject = new TokenSubject(
                claims.userId(),
                claims.username(),
                claims.roles()
        );

        AccessToken newAccessToken = jwtUtil.generateAccessToken(subject);
        RefreshToken newRefreshToken = jwtUtil.generateRefreshToken(subject);

        refreshTokenService.rotateRefreshToken(
                subject.userId(),
                DEFAULT_DEVICE_ID,
                newRefreshToken.token(),
                newRefreshToken.jti(),
                newRefreshToken.expiresAt()
        );

        return AuthTokens.of(subject, newAccessToken, newRefreshToken);
    }

    public boolean hasValidSession(String refreshToken) {
        // 앱 시작 시 세션 복구 가능 여부만 확인하므로, 무효한 refresh 상태는 false로 처리한다.
        try {
            validateRefreshToken(refreshToken);
            return true;
        } catch (CustomException exception) {
            if (isInvalidSessionError(exception.getErrorCode())) {
                return false;
            }

            throw exception;
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshTokenClaims claims = validateRefreshToken(refreshToken);
        refreshTokenService.deleteRefreshToken(claims.userId(), DEFAULT_DEVICE_ID);
    }

    private RefreshTokenClaims validateRefreshToken(String refreshToken) {
        // session 확인, refresh, logout이 같은 refresh token 검증 규칙을 사용하도록 공통화한다.
        String rawRefreshToken = requireRefreshToken(refreshToken);
        RefreshTokenClaims claims = jwtUtil.parseRefreshToken(rawRefreshToken);

        refreshTokenService.validateRefreshToken(rawRefreshToken, claims, DEFAULT_DEVICE_ID);
        return claims;
    }

    private boolean isInvalidSessionError(ErrorCode errorCode) {
        return switch (errorCode) {
            case REFRESH_TOKEN_REQUIRED,
                 INVALID_REFRESH_TOKEN,
                 EXPIRED_REFRESH_TOKEN,
                 INVALID_REFRESH_TOKEN_TYPE,
                 REFRESH_TOKEN_NOT_FOUND_IN_STORE,
                 REFRESH_TOKEN_REUSE_DETECTED -> true;
            default -> false;
        };
    }

    private String requireRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        return refreshToken.trim();
    }
}
