package com.toy.project.studio.setting.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import com.toy.project.studio.setting.entity.StudioSetting;

public record StudioSettingDetailResponse(
        Long id,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime lunchStart,
        LocalTime lunchEnd,
        Integer slotUnit,
        Integer reservationOpenDays,
        List<LocalDate> closedDays
) {

    public static StudioSettingDetailResponse from(
            StudioSetting studioSetting,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new StudioSettingDetailResponse(
                studioSetting.getId(),
                studioSetting.getOpenTime(),
                studioSetting.getCloseTime(),
                studioSetting.getLunchStart(),
                studioSetting.getLunchEnd(),
                studioSetting.getSlotUnit(),
                studioSetting.getReservationOpenDays(),
                resolveClosedDays(studioSetting, startDate, endDate)
        );
    }

    private static List<LocalDate> resolveClosedDays(
            StudioSetting studioSetting,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                .filter(date -> isClosedDay(studioSetting, date.getDayOfWeek()))
                .toList();
    }

    private static boolean isClosedDay(StudioSetting studioSetting, DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> studioSetting.isClosedMon();
            case TUESDAY -> studioSetting.isClosedTue();
            case WEDNESDAY -> studioSetting.isClosedWed();
            case THURSDAY -> studioSetting.isClosedThu();
            case FRIDAY -> studioSetting.isClosedFri();
            case SATURDAY -> studioSetting.isClosedSat();
            case SUNDAY -> studioSetting.isClosedSun();
        };
    }
}
