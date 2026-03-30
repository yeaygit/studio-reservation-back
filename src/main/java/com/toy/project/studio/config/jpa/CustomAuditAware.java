package com.toy.project.studio.config.jpa;

import java.util.Optional;

import com.toy.project.studio.config.jwt.JwtPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * createdBy/modifiedBy 에 들어갈 감사 주체 이름을 결정한다.
 *
 * 우선순위
 * 1. 로그인된 관리자 username
 * 2. 비로그인 요청에서 직접 넣어둔 이름
 * 3. 아무 정보가 없으면 "user"
 */
@Component("customAuditAware")
public class CustomAuditAware implements org.springframework.data.domain.AuditorAware<String> {

    private static final String DEFAULT_AUDITOR = "user";

    @Override
    public Optional<String> getCurrentAuditor() {
        // Spring Security 인증 정보가 있으면 관리자가 로그인한 요청으로 본다.
        return resolveAuthenticatedUsername()
                // 비로그인 요청은 요청 처리 중에 AuditActorContext.setCurrentActor(...)로 이름을 심어둘 수 있다.
                .or(AuditActorContext::getCurrentActor)
                // 끝까지 없으면 빈 값 대신 공통 기본값을 사용한다.
                .or(() -> Optional.of(DEFAULT_AUDITOR));
    }

    private Optional<String> resolveAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        // JwtPrincipal에서 username만 꺼낸다
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtPrincipal jwtPrincipal) {
            String username = jwtPrincipal.username(); // record면 .username(), 클래스면 .getUsername()
            return StringUtils.hasText(username) ? Optional.of(username) : Optional.empty();
        }

        // fallback: principal이 String인 경우
        String username = authentication.getName();
        return StringUtils.hasText(username) ? Optional.of(username) : Optional.empty();

    }
}
