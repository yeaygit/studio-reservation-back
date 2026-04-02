package com.toy.project.studio.setting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;
import com.toy.project.studio.setting.entity.ClosedDay;
import com.toy.project.studio.setting.entity.StudioSetting;
import com.toy.project.studio.setting.enumeration.ClosedDayType;
import com.toy.project.studio.setting.repository.ClosedDayRepository;
import com.toy.project.studio.setting.repository.StudioSettingRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

    @Mock
    private StudioSettingRepository studioSettingRepository;

    @Mock
    private ClosedDayRepository closedDayRepository;

    @InjectMocks
    private SettingService settingService;

    @Test
    void getStudioSettingReturnsClosedDaysAsMonToSunList() {
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

        when(studioSettingRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(studioSetting));

        StudioSettingDetailResponse response = settingService.getStudioSetting();

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.openTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.closeTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(response.lunchStart()).isEqualTo(LocalTime.of(12, 0));
        assertThat(response.lunchEnd()).isEqualTo(LocalTime.of(13, 0));
        assertThat(response.slotUnit()).isEqualTo(30);
        assertThat(response.reservationOpenDays()).isEqualTo(14);
        assertThat(response.closedDays()).containsExactly("MON", "WED", "FRI", "SUN");
    }

    @Test
    void getClosedDaysReturnsSortedDistinctDatesWithinRange() {
        ClosedDay annualNewYear = ClosedDay.builder()
                .id(1L)
                .type(ClosedDayType.ANNUAL)
                .annualMonth(1)
                .annualDay(1)
                .build();
        ClosedDay annualYearEnd = ClosedDay.builder()
                .id(2L)
                .type(ClosedDayType.ANNUAL)
                .annualMonth(12)
                .annualDay(31)
                .build();
        ClosedDay specificDuplicate = ClosedDay.builder()
                .id(3L)
                .type(ClosedDayType.SPECIFIC)
                .specificDate(LocalDate.of(2026, 1, 1))
                .build();

        when(closedDayRepository.findAllActiveByDateRange(
                LocalDate.of(2025, 12, 30),
                LocalDate.of(2026, 1, 2),
                ClosedDayType.SPECIFIC,
                ClosedDayType.ANNUAL
        )).thenReturn(List.of(annualNewYear, annualYearEnd, specificDuplicate));

        List<LocalDate> response = settingService.getClosedDays(
                LocalDate.of(2025, 12, 30),
                LocalDate.of(2026, 1, 2)
        );

        assertThat(response).containsExactly(
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2026, 1, 1)
        );
    }

    @Test
    void getClosedDaysSkipsInvalidLegacyAnnualData() {
        ClosedDay invalidAnnualClosedDay = ClosedDay.builder()
                .id(1L)
                .type(ClosedDayType.ANNUAL)
                .annualMonth(13)
                .annualDay(1)
                .build();
        ClosedDay validSpecificClosedDay = ClosedDay.builder()
                .id(2L)
                .type(ClosedDayType.SPECIFIC)
                .specificDate(LocalDate.of(2025, 5, 10))
                .build();

        when(closedDayRepository.findAllActiveByDateRange(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 31),
                ClosedDayType.SPECIFIC,
                ClosedDayType.ANNUAL
        )).thenReturn(List.of(invalidAnnualClosedDay, validSpecificClosedDay));

        List<LocalDate> response = settingService.getClosedDays(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 31)
        );

        assertThat(response).containsExactly(LocalDate.of(2025, 5, 10));
    }

    @Test
    void getClosedDaysThrowsWhenStartDateIsAfterEndDate() {
        assertThatThrownBy(() -> settingService.getClosedDays(
                LocalDate.of(2025, 5, 11),
                LocalDate.of(2025, 5, 10)
        )).isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }
}
