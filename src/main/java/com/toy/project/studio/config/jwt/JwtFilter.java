package com.toy.project.studio.config.jwt;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    // refresh/logout/login은 access token 필터 대상이 아닙니다.
    // refresh token은 컨트롤러/서비스에서만 다루기 위해 여기서 제외합니다.
    private static final Set<String> AUTH_ENDPOINTS = Set.of(
            "/auth/login",
            "/auth/refresh",
            "/auth/logout"
    );

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(
            JwtUtil jwtUtil,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtUtil = jwtUtil;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return AUTH_ENDPOINTS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // access token이 아예 없으면 익명 요청으로 그대로 넘깁니다.
        if (!StringUtils.hasText(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 형식이 아니면 access token 전달 방식 자체가 잘못된 것입니다.
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            resolveException(request, response, new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));
            return;
        }

        String accessToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(accessToken)) {
            resolveException(request, response, new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));
            return;
        }

        try {
            // parseAccessToken() 안에서 서명, 만료, tokenType(access)를 모두 검증합니다.
            Authentication authentication = jwtUtil.createAuthentication(jwtUtil.parseAccessToken(accessToken));

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 인증에 성공하면 SecurityContext에 넣어 이후 인가 로직이 사용할 수 있게 합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (CustomException exception) {
            // 잘못된 access token이면 여기서 바로 예외 응답을 내려줍니다.
            resolveException(request, response, exception);
        }
    }

    private void resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            CustomException exception
    ) {
        SecurityContextHolder.clearContext();
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}
