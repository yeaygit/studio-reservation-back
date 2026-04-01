package com.toy.project.studio.setting.entity;

import java.time.LocalTime;

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
@Table(name = "studio_setting")
public class StudioSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("Open time")
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Comment("Close time")
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Comment("Lunch start time")
    @Column(name = "lunch_start", nullable = false)
    private LocalTime lunchStart;

    @Comment("Lunch end time")
    @Column(name = "lunch_end", nullable = false)
    private LocalTime lunchEnd;

    @Comment("Reservation slot unit")
    @Column(name = "slot_unit", nullable = false)
    @Builder.Default
    private Integer slotUnit = 30;

    @Comment("예약가능 오픈일수")
    @Column(name = "reservation_open_days", nullable = false)
    @Builder.Default
    private Integer reservationOpenDays = 30;


    @Comment("Sunday closed")
    @Column(name = "closed_sun", nullable = false)
    private boolean closedSun;

    @Comment("Monday closed")
    @Column(name = "closed_mon", nullable = false)
    private boolean closedMon;

    @Comment("Tuesday closed")
    @Column(name = "closed_tue", nullable = false)
    private boolean closedTue;

    @Comment("Wednesday closed")
    @Column(name = "closed_wed", nullable = false)
    private boolean closedWed;

    @Comment("Thursday closed")
    @Column(name = "closed_thu", nullable = false)
    private boolean closedThu;

    @Comment("Friday closed")
    @Column(name = "closed_fri", nullable = false)
    private boolean closedFri;

    @Comment("Saturday closed")
    @Column(name = "closed_sat", nullable = false)
    private boolean closedSat;

    public void update(
            LocalTime openTime,
            LocalTime closeTime,
            LocalTime lunchStart,
            LocalTime lunchEnd,
            Integer slotUnit,
            Integer reservationOpenDays,
            boolean closedSun,
            boolean closedMon,
            boolean closedTue,
            boolean closedWed,
            boolean closedThu,
            boolean closedFri,
            boolean closedSat
    ) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        this.slotUnit = slotUnit;
        this.reservationOpenDays = reservationOpenDays;
        this.closedSun = closedSun;
        this.closedMon = closedMon;
        this.closedTue = closedTue;
        this.closedWed = closedWed;
        this.closedThu = closedThu;
        this.closedFri = closedFri;
        this.closedSat = closedSat;
    }
}
