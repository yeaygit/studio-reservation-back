package com.toy.project.studio.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.reservation.dto.response.ReservationAdminResponse;
import com.toy.project.studio.reservation.service.ReservationAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    @GetMapping
    public ResponseEntity<List<ReservationAdminResponse>> getReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "") String name
    ) {
        return ResponseEntity.ok(reservationAdminService.getReservations(date, name));
    }
}
