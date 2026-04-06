package com.toy.project.studio.setting.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.toy.project.studio.setting.entity.StudioSetting;

class StudioSettingDetailResponseTest {

    @Test
    void fromReturnsClosedDaysAsLocalDatesWithinRequestedRange() {
        StudioSetting studioSetting = StudioSetting.builder()
                .id(1L)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(18, 0))
                .lunchStart(LocalTime.of(12, 0))
                .lunchEnd(LocalTime.of(13, 0))
                .slotUnit(30)
                .reservationOpenDays(14)
                .closedSun(true)
                .closedMon(true)
                .closedTue(false)
                .closedWed(true)
                .closedThu(false)
                .closedFri(true)
                .closedSat(false)
                .build();

        StudioSettingDetailResponse response = StudioSettingDetailResponse.from(
                studioSetting,
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 4, 12)
        );

        assertThat(response.closedDays()).containsExactly(
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 4, 5),
                LocalDate.of(2026, 4, 6),
                LocalDate.of(2026, 4, 8),
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 12)
        );
    }
}
