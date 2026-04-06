package com.toy.project.studio.reservation.dto.response;

import com.toy.project.studio.reservation.entity.Reservation;

public record ReservationCreateResponse(
        Long reservationId
) {

    public static ReservationCreateResponse from(Reservation reservation) {
        return new ReservationCreateResponse(reservation.getId());
    }
}
