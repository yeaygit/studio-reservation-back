package com.toy.project.studio.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.reservation.dto.request.ReservationCreateRequest;
import com.toy.project.studio.reservation.dto.response.ReservationCreateResponse;
import com.toy.project.studio.reservation.dto.response.ReservedTimeResponse;
import com.toy.project.studio.reservation.dto.response.ReservationSettingResponse;
import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.repository.ReservationRepository;
import com.toy.project.studio.reservation.repository.ReservedTimeProjection;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;
import com.toy.project.studio.setting.service.SettingService;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private SettingService settingService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ShootingTypeRepository shootingTypeRepository;

    @Mock
    private TermsRepository termsRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-03T00:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        reservationService = new ReservationService(
                settingService,
                reservationRepository,
                shootingTypeRepository,
                termsRepository,
                fixedClock
        );
    }

    @Test
    void getReservationSettingReturnsClosedDaysAndBlockedDaysWithinReservationWindow() {
        StudioSettingDetailResponse studioSetting = new StudioSettingDetailResponse(
                1L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                30,
                14,
                List.of(
                        LocalDate.of(2026, 4, 3),
                        LocalDate.of(2026, 4, 6),
                        LocalDate.of(2026, 4, 8)
                )
        );

        when(settingService.getStudioSetting(LocalDate.of(2026, 4, 3))).thenReturn(studioSetting);
        when(settingService.getClosedDays(
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 4, 17)
        )).thenReturn(List.of(
                LocalDate.of(2026, 4, 5),
                LocalDate.of(2026, 4, 9)
        ));

        ReservationSettingResponse response = reservationService.getReservationSetting();

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.openTime()).isEqualTo(540);
        assertThat(response.closeTime()).isEqualTo(1080);
        assertThat(response.lunchStart()).isEqualTo(720);
        assertThat(response.lunchEnd()).isEqualTo(780);
        assertThat(response.slotUnit()).isEqualTo(30);
        assertThat(response.reservationOpenDays()).isEqualTo(14);
        assertThat(response.closedDays()).containsExactly(
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 4, 6),
                LocalDate.of(2026, 4, 8)
        );
        assertThat(response.blockedDays()).containsExactly(
                LocalDate.of(2026, 4, 5),
                LocalDate.of(2026, 4, 9)
        );
    }

    @Test
    void getReservationsByDateReturnsReservedTimesInMinutes() {
        LocalDate reservationDate = LocalDate.of(2026, 4, 10);

        ReservedTimeProjection morningReservation = reservedTimeProjection(2L, LocalTime.of(10, 30), LocalTime.of(11, 0));
        ReservedTimeProjection afternoonReservation = reservedTimeProjection(1L, LocalTime.of(18, 0), LocalTime.of(20, 0));

        when(reservationRepository.findBookedTimesByDate(reservationDate))
                .thenReturn(List.of(morningReservation, afternoonReservation));

        List<ReservedTimeResponse> response = reservationService.getReservationsByDate(reservationDate);

        assertThat(response).containsExactly(
                new ReservedTimeResponse(2L, 630, 660),
                new ReservedTimeResponse(1L, 1080, 1200)
        );
    }

    @Test
    void getReservationsByDateReturnsEmptyListWhenNoReservationsExist() {
        LocalDate reservationDate = LocalDate.of(2026, 4, 10);

        when(reservationRepository.findBookedTimesByDate(reservationDate)).thenReturn(List.of());

        List<ReservedTimeResponse> response = reservationService.getReservationsByDate(reservationDate);

        assertThat(response).isEmpty();
    }

    @Test
    void createReservationSavesReservationAndReturnsReservationId() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "id",
                LocalDate.of(2026, 4, 10),
                1,
                LocalTime.of(19, 30),
                LocalTime.of(20, 0),
                " 김유림 ",
                " 010-6614-9926 ",
                " naver ",
                " ",
                List.of(1L, 2L)
        );

        ShootingType shootingType = ShootingType.builder()
                .id(10L)
                .code("id")
                .label("ID")
                .duration(30L)
                .price(10000L)
                .build();

        when(shootingTypeRepository.findActiveByCode("id")).thenReturn(java.util.Optional.of(shootingType));
        when(termsRepository.findActiveIdsByIdIn(List.of(1L, 2L))).thenReturn(List.of(1L, 2L));
        when(termsRepository.findRequiredActiveIds()).thenReturn(List.of(1L, 2L));
        when(reservationRepository.save(org.mockito.ArgumentMatchers.any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0, Reservation.class);
            return Reservation.builder()
                    .id(1L)
                    .shootingType(reservation.getShootingType())
                    .date(reservation.getDate())
                    .startTime(reservation.getStartTime())
                    .endTime(reservation.getEndTime())
                    .headCount(reservation.getHeadCount())
                    .name(reservation.getName())
                    .phone(reservation.getPhone())
                    .visitPath(reservation.getVisitPath())
                    .requestMessage(reservation.getRequestMessage())
                    .build();
        });

        ReservationCreateResponse response = reservationService.createReservation(request);

        assertThat(response.reservationId()).isEqualTo(1L);
    }

    @Test
    void createReservationThrowsWhenRequiredTermsAreMissing() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "id",
                LocalDate.of(2026, 4, 10),
                1,
                LocalTime.of(19, 30),
                LocalTime.of(20, 0),
                "김유림",
                "010-6614-9926",
                "naver",
                "",
                List.of(1L)
        );

        when(termsRepository.findActiveIdsByIdIn(List.of(1L))).thenReturn(List.of(1L));
        when(termsRepository.findRequiredActiveIds()).thenReturn(List.of(1L, 2L));

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    private ReservedTimeProjection reservedTimeProjection(Long reservationId, LocalTime startTime, LocalTime endTime) {
        return new ReservedTimeProjection() {
            @Override
            public Long getReservationId() {
                return reservationId;
            }

            @Override
            public LocalTime getStartTime() {
                return startTime;
            }

            @Override
            public LocalTime getEndTime() {
                return endTime;
            }
        };
    }
}
