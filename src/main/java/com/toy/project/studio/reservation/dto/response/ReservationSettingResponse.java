package com.toy.project.studio.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;

public record ReservationSettingResponse(
        Long id,
        Integer openTime,
        Integer closeTime,
        Integer lunchStart,
        Integer lunchEnd,
        Integer slotUnit,
        Integer reservationOpenDays,
        List<LocalDate> closedDays,
        List<LocalDate> blockedDays
) {

    public static ReservationSettingResponse from(
            StudioSettingDetailResponse studioSetting,
            List<LocalDate> closedDays,
            List<LocalDate> blockedDays
    ) {
        return new ReservationSettingResponse(
                studioSetting.id(),
                toMinutes(studioSetting.openTime()),
                toMinutes(studioSetting.closeTime()),
                toMinutes(studioSetting.lunchStart()),
                toMinutes(studioSetting.lunchEnd()),
                studioSetting.slotUnit(),
                studioSetting.reservationOpenDays(),
                List.copyOf(closedDays),
                List.copyOf(blockedDays)
        );
    }

    private static Integer toMinutes(LocalTime time) {
        if (time == null) return null;
        return time.getHour() * 60 + time.getMinute();
    }
}
