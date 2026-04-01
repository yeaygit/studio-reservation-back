package com.toy.project.studio.setting.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StudioSettingUpdateRequest(
        @NotNull(message = "openTime은 필수입니다.") LocalTime openTime,
        @NotNull(message = "closeTime은 필수입니다.") LocalTime closeTime,
        @NotNull(message = "lunchStart는 필수입니다.") LocalTime lunchStart,
        @NotNull(message = "lunchEnd는 필수입니다.") LocalTime lunchEnd,
        @NotNull(message = "slotUnit은 필수입니다.")
        @Positive(message = "slotUnit은 0보다 커야 합니다.")
        Integer slotUnit,
        @NotNull(message = "예약가능오픈일수는 필수입니다.")
        @Positive(message = "예약가능오픈일수는 0보다 커야 합니다.")
        Integer reservationOpenDays,
        @NotNull(message = "closedSun은 필수입니다.") Boolean closedSun,
        @NotNull(message = "closedMon은 필수입니다.") Boolean closedMon,
        @NotNull(message = "closedTue은 필수입니다.") Boolean closedTue,
        @NotNull(message = "closedWed은 필수입니다.") Boolean closedWed,
        @NotNull(message = "closedThu은 필수입니다.") Boolean closedThu,
        @NotNull(message = "closedFri은 필수입니다.") Boolean closedFri,
        @NotNull(message = "closedSat은 필수입니다.") Boolean closedSat
) {
}
