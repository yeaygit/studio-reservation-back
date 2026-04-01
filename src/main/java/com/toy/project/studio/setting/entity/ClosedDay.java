package com.toy.project.studio.setting.entity;

import java.time.LocalDate;

import com.toy.project.studio.config.jpa.BaseEntity;
import com.toy.project.studio.setting.enumeration.ClosedDayType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "closed_day")
public class ClosedDay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("Closed day type (SPECIFIC, ANNUAL)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ClosedDayType type = ClosedDayType.ANNUAL;

    @Comment("Date for SPECIFIC type")
    @Column(name = "date")
    private LocalDate specificDate;

    @Comment("Month for ANNUAL type")
    @Column(name = "month")
    private Integer annualMonth;

    @Comment("Day for ANNUAL type")
    @Column(name = "day")
    private Integer annualDay;

    @Comment("Active flag")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
