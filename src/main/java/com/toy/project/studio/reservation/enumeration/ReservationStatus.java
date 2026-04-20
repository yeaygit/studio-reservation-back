package com.toy.project.studio.reservation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {

    PENDING("PENDING", "대기"),
    CONFIRMED("CONFIRMED", "확정"),
    CANCELLED("CANCELLED", "취소");

    private String code;
    private String label;

}
