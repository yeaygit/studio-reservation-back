package com.toy.project.studio.terms.entity;

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
@Table(name = "terms")
public class Terms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("Terms title")
    @Column(nullable = false, length = 200)
    private String title;

    @Comment("Terms content")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Comment("Required flag")
    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private boolean isRequired = true;

    @Comment("Active flag")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public void update(String title, String content, boolean isRequired) {
        this.title = title;
        this.content = content;
        this.isRequired = isRequired;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
