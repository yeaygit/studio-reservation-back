package com.toy.project.studio.reservation.entity;

import com.toy.project.studio.config.jpa.BaseEntity;
import com.toy.project.studio.reservation.enumeration.ReservationStatus;
import com.toy.project.studio.reservation.enumeration.VisitPath;
import com.toy.project.studio.setting.entity.ShootingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reservation")
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shooting_type_id", nullable = false)
    private ShootingType shootingType;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "head_count", nullable = false)
    @Builder.Default
    private Integer headCount = 1;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "visit_path", length = 20)
    @Enumerated(EnumType.STRING)
    private VisitPath visitPath;

    @Column(name = "request_message")
    private String requestMessage;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;
}
