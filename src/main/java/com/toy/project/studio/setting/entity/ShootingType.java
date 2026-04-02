package com.toy.project.studio.setting.entity;

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
@Table(name = "shooting_type")
public class ShootingType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("Code(Enum)")
    @Column(nullable = false, length = 20)
    private String code;

    @Comment("Shooting type label")
    @Column(nullable = false, length = 50)
    private String label;

    @Comment("Duration in minutes")
    @Column(nullable = false)
    private Long duration;

    @Comment("Price")
    @Column(nullable = false)
    private Long price;

    @Comment("Shooting type description")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Comment("Active flag")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Comment("Sort order")
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Long sortOrder = 0L;

    public void update(
            String label,
            Long duration,
            Long price,
            String description,
            Long sortOrder
    ) {
        this.label = label;
        this.duration = duration;
        this.price = price;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
