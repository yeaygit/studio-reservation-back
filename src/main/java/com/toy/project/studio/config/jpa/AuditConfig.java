package com.toy.project.studio.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing을 활성화하고, createdBy/modifiedBy 값을 누가 채울지 연결한다.
 * 실제 사용자 이름 결정은 CustomAuditAware가 담당한다.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditAware")
public class AuditConfig {
}
