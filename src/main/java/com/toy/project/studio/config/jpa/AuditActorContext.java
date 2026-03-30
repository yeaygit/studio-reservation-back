package com.toy.project.studio.config.jpa;

import java.util.Optional;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 비로그인 요청에서만 잠깐 사용할 감사 작성자 이름을 요청 범위에 보관한다.
 *
 * 예시
 * 저장 직전에 AuditActorContext.setCurrentActor(request.name());
 * 그 요청 안에서 Auditing이 동작하면 createdBy/modifiedBy 에 같은 이름이 들어간다.
 */
public final class AuditActorContext {

    public static final String ATTRIBUTE_NAME = "auditActor";

    private AuditActorContext() {
    }


    public static Optional<String> getCurrentActor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return Optional.empty();
        }

        Object actor = requestAttributes.getAttribute(ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        if (actor instanceof String actorName && StringUtils.hasText(actorName)) {
            return Optional.of(actorName.trim());
        }

        return Optional.empty();
    }


}
