package com.toy.project.studio.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.reservation.dto.response.ReservationAdminResponse;
import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationAdminService {

    private final ReservationRepository reservationRepository;

    public List<ReservationAdminResponse> getReservations(LocalDate date, String name) {
        String namePattern = buildNamePattern(name);

        // 날짜는 항상 고정이고, 이름 필터만 선택적으로 붙인다.
        return findReservations(date, hasText(name), namePattern).stream()
                .map(ReservationAdminResponse::from)
                .toList();
    }

    private List<Reservation> findReservations(
            LocalDate date,
            boolean hasNameFilter,
            String namePattern
    ) {
        if (hasNameFilter) {
            return reservationRepository.findAllWithShootingTypeByDateAndNamePatternOrderByStartTimeAsc(
                    date,
                    namePattern
            );
        }

        return reservationRepository.findAllWithShootingTypeByDateOrderByStartTimeAsc(date);
    }

    private String buildNamePattern(String name) {
        if (!hasText(name)) {
            return null;
        }

        return "%" + name.trim().toLowerCase(Locale.ROOT) + "%";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
