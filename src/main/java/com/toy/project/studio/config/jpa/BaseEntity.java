package com.toy.project.studio.config.jpa;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * 모든 엔티티가 공통으로 사용할 생성/수정 감사 필드다.
 * 값은 CustomAuditAware가 결정하고, JPA Auditing이 저장 시점에 자동으로 채운다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 처음 저장한 사용자 이름이다.
     * 로그인 관리자면 username, 비로그인 요청이면 전달한 이름 또는 기본값 user가 들어간다.
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /**
     * 처음 저장된 시각이다.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 마지막으로 수정한 사용자 이름이다.
     */
    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    /**
     * 마지막으로 수정된 시각이다.
     */
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
