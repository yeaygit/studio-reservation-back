package com.toy.project.studio.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.reservation.dto.response.ReservationAdminResponse;
import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.enumeration.ReservationStatus;
import com.toy.project.studio.reservation.enumeration.VisitPath;
import com.toy.project.studio.reservation.repository.ReservationRepository;
import com.toy.project.studio.setting.entity.ShootingType;

@ExtendWith(MockitoExtension.class)
class ReservationAdminServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationAdminService reservationAdminService;

    @Test
    void getReservationsReturnsAdminResponses() {
        ShootingType profileType = ShootingType.builder()
                .id(1L)
                .code("profile")
                .label("Profile")
                .duration(30L)
                .price(30000L)
                .build();

        Reservation firstReservation = Reservation.builder()
                .id(10L)
                .shootingType(profileType)
                .date(LocalDate.of(2026, 4, 20))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(14, 30))
                .headCount(2)
                .name("hong")
                .phone("010-1234-5678")
                .visitPath(VisitPath.instagram)
                .requestMessage("white background")
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation secondReservation = Reservation.builder()
                .id(9L)
                .shootingType(profileType)
                .date(LocalDate.of(2026, 4, 18))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .headCount(1)
                .name("kim")
                .phone("010-9999-8888")
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepository.findAllWithShootingTypeByDateOrderByDateDescStartTimeAsc(
                eq(LocalDate.of(2026, 4, 20))
        ))
                .thenReturn(List.of(firstReservation, secondReservation));

        List<ReservationAdminResponse> response = reservationAdminService.getReservations(
                LocalDate.of(2026, 4, 20),
                null
        );

        assertThat(response).hasSize(2);
        assertThat(response.get(0).reservationId()).isEqualTo(10L);
        assertThat(response.get(0).shootingTypeCode()).isEqualTo("profile");
        assertThat(response.get(0).shootingTypeLabel()).isEqualTo("Profile");
        assertThat(response.get(0).date()).isEqualTo(LocalDate.of(2026, 4, 20));
        assertThat(response.get(0).startTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(response.get(0).endTime()).isEqualTo(LocalTime.of(14, 30));
        assertThat(response.get(0).headCount()).isEqualTo(2);
        assertThat(response.get(0).name()).isEqualTo("hong");
        assertThat(response.get(0).phone()).isEqualTo("010-1234-5678");
        assertThat(response.get(0).visitPath()).isEqualTo(VisitPath.instagram.getLabel());
        assertThat(response.get(0).requestMessage()).isEqualTo("white background");
        assertThat(response.get(0).status()).isEqualTo(ReservationStatus.CONFIRMED);

        assertThat(response.get(1).reservationId()).isEqualTo(9L);
        assertThat(response.get(1).name()).isEqualTo("kim");
        assertThat(response.get(1).status()).isEqualTo(ReservationStatus.PENDING);

        verify(reservationRepository).findAllWithShootingTypeByDateOrderByDateDescStartTimeAsc(
                LocalDate.of(2026, 4, 20)
        );
    }

    @Test
    void getReservationsAppliesDateAndNameFilters() {
        when(reservationRepository.findAllWithShootingTypeByDateAndNamePatternOrderByDateDescStartTimeDesc(
                eq(LocalDate.of(2026, 4, 20)),
                eq("%hong%")
        )).thenReturn(List.of());

        List<ReservationAdminResponse> response = reservationAdminService.getReservations(
                LocalDate.of(2026, 4, 20),
                "  hong  "
        );

        assertThat(response).isEmpty();
        verify(reservationRepository).findAllWithShootingTypeByDateAndNamePatternOrderByDateDescStartTimeDesc(
                LocalDate.of(2026, 4, 20),
                "%hong%"
        );
    }

    @Test
    void getReservationsAppliesDateFilterOnly() {
        when(reservationRepository.findAllWithShootingTypeByDateOrderByDateDescStartTimeAsc(
                eq(LocalDate.of(2026, 4, 20))
        )).thenReturn(List.of());

        List<ReservationAdminResponse> response = reservationAdminService.getReservations(
                LocalDate.of(2026, 4, 20),
                null
        );

        assertThat(response).isEmpty();
        verify(reservationRepository).findAllWithShootingTypeByDateOrderByDateDescStartTimeAsc(
                LocalDate.of(2026, 4, 20)
        );
    }
}
