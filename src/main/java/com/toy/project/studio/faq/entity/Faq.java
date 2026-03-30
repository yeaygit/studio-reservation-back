package com.toy.project.studio.faq.entity;

import com.toy.project.studio.config.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "faq")
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("질문")
    @Column(nullable = false)
    private String question;

    @Comment("답변")
    @Column(nullable = false)
    private String answer;

    @Comment("정렬 순서")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Comment("사용 여부")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public void update(String question, String answer, Integer sortOrder) {
        this.question = question;
        this.answer = answer;
        this.sortOrder = sortOrder;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
