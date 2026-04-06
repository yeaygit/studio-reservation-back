package com.toy.project.studio.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.reservation.dto.request.ReservationCreateRequest;
import com.toy.project.studio.reservation.dto.response.ReservationCreateResponse;
import com.toy.project.studio.reservation.dto.response.ReservedTimeResponse;
import com.toy.project.studio.reservation.dto.response.ReservationSettingResponse;
import com.toy.project.studio.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/setting")
    public ResponseEntity<ReservationSettingResponse> getReservationSetting() {
        return ResponseEntity.ok(reservationService.getReservationSetting());
    }

    @PostMapping
    public ResponseEntity<ReservationCreateResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    @GetMapping("/booked-times")
    public ResponseEntity<List<ReservedTimeResponse>> getBookedReservationTimesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(date));
    }
}
