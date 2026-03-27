package com.toy.project.studio.auth.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.toy.project.studio.auth.dto.AuthTokens;
import com.toy.project.studio.auth.dto.LoginRequest;
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
        String rawRefreshToken = requireRefreshToken(refreshToken);
        RefreshTokenClaims claims = jwtUtil.parseRefreshToken(rawRefreshToken);

        refreshTokenService.validateRefreshToken(rawRefreshToken, claims, DEFAULT_DEVICE_ID);

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

    @Transactional
    public void logout(String refreshToken) {
        String rawRefreshToken = requireRefreshToken(refreshToken);
        RefreshTokenClaims claims = jwtUtil.parseRefreshToken(rawRefreshToken);

        refreshTokenService.validateRefreshToken(rawRefreshToken, claims, DEFAULT_DEVICE_ID);
        refreshTokenService.deleteRefreshToken(claims.userId(), DEFAULT_DEVICE_ID);
    }

    private String requireRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        return refreshToken.trim();
    }
}