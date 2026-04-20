package com.toy.project.studio.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.enumeration.ReservationStatus;
import com.toy.project.studio.reservation.enumeration.VisitPath;

public record ReservationAdminResponse(
        Long reservationId,
        String shootingTypeCode,
        String shootingTypeLabel,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,
        Integer headCount,
        String name,
        String phone,
        String visitPath,
        String requestMessage,
        ReservationStatus status,
        String createdAt
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ReservationAdminResponse from(Reservation reservation) {
        return new ReservationAdminResponse(
                reservation.getId(),
                reservation.getShootingType().getCode(),
                reservation.getShootingType().getLabel(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getHeadCount(),
                reservation.getName(),
                reservation.getPhone(),
                toVisitPathLabel(reservation.getVisitPath()),
                reservation.getRequestMessage(),
                reservation.getStatus(),
                formatDateTime(reservation.getCreatedAt())
        );
    }

    private static String toVisitPathLabel(VisitPath visitPath) {
        return visitPath == null ? null : visitPath.getLabel();
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }
}
