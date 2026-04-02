package com.toy.project.studio.setting.dto.response;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.toy.project.studio.setting.entity.StudioSetting;

public record StudioSettingDetailResponse(
        Long id,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime lunchStart,
        LocalTime lunchEnd,
        Integer slotUnit,
        Integer reservationOpenDays,
        List<String> closedDays
) {

    public static StudioSettingDetailResponse from(StudioSetting studioSetting) {
        return new StudioSettingDetailResponse(
                studioSetting.getId(),
                studioSetting.getOpenTime(),
                studioSetting.getCloseTime(),
                studioSetting.getLunchStart(),
                studioSetting.getLunchEnd(),
                studioSetting.getSlotUnit(),
                studioSetting.getReservationOpenDays(),
                resolveClosedDays(studioSetting)
        );
    }

    private static List<String> resolveClosedDays(StudioSetting studioSetting) {
        List<String> closedDays = new ArrayList<>();

        if (studioSetting.isClosedMon()) {
            closedDays.add("MON");
        }
        if (studioSetting.isClosedTue()) {
            closedDays.add("TUE");
        }
        if (studioSetting.isClosedWed()) {
            closedDays.add("WED");
        }
        if (studioSetting.isClosedThu()) {
            closedDays.add("THU");
        }
        if (studioSetting.isClosedFri()) {
            closedDays.add("FRI");
        }
        if (studioSetting.isClosedSat()) {
            closedDays.add("SAT");
        }
        if (studioSetting.isClosedSun()) {
            closedDays.add("SUN");
        }

        return closedDays;
    }
}
