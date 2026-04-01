package com.toy.project.studio.setting.dto.response;

import java.time.LocalTime;

import com.toy.project.studio.setting.entity.StudioSetting;

public record StudioSettingResponse(
        Long id,
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
    public static StudioSettingResponse from(StudioSetting studioSetting) {
        return new StudioSettingResponse(
                studioSetting.getId(),
                studioSetting.getOpenTime(),
                studioSetting.getCloseTime(),
                studioSetting.getLunchStart(),
                studioSetting.getLunchEnd(),
                studioSetting.getSlotUnit(),
                studioSetting.getReservationOpenDays(),
                studioSetting.isClosedSun(),
                studioSetting.isClosedMon(),
                studioSetting.isClosedTue(),
                studioSetting.isClosedWed(),
                studioSetting.isClosedThu(),
                studioSetting.isClosedFri(),
                studioSetting.isClosedSat()
        );
    }
}
