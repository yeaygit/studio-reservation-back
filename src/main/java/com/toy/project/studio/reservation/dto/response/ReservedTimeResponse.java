package com.toy.project.studio.reservation.dto.response;

import java.time.LocalTime;

import com.toy.project.studio.reservation.repository.ReservedTimeProjection;

public record ReservedTimeResponse(
        Long reservationId,
        Integer startTime,
        Integer endTime
) {

    public static ReservedTimeResponse from(ReservedTimeProjection reservation) {
        return new ReservedTimeResponse(
                reservation.getReservationId(),
                toMinutes(reservation.getStartTime()),
                toMinutes(reservation.getEndTime())
        );
    }

    private static Integer toMinutes(LocalTime time) {
        if (time == null) return null;
        return time.getHour() * 60 + time.getMinute();
    }
}
