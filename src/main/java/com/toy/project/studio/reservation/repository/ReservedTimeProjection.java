package com.toy.project.studio.reservation.repository;

import java.time.LocalTime;

public interface ReservedTimeProjection {

    Long getReservationId();

    LocalTime getStartTime();

    LocalTime getEndTime();
}
