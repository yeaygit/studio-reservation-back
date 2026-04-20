package com.toy.project.studio.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.reservation.dto.request.ReservationCreateRequest;
import com.toy.project.studio.reservation.dto.response.ReservationCreateResponse;
import com.toy.project.studio.reservation.dto.response.ReservedTimeResponse;
import com.toy.project.studio.reservation.dto.response.ReservationSettingResponse;
import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.enumeration.ReservationStatus;
import com.toy.project.studio.reservation.enumeration.VisitPath;
import com.toy.project.studio.reservation.repository.ReservationRepository;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;
import com.toy.project.studio.setting.service.SettingService;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    // 취소된 예약은 같은 시간대 신청을 막을 필요가 없어서 제외한다.
    private static final Set<ReservationStatus> BLOCKING_STATUSES = Set.of(ReservationStatus.CONFIRMED);

    private final SettingService settingService;
    private final ReservationRepository reservationRepository;
    private final ShootingTypeRepository shootingTypeRepository;
    private final TermsRepository termsRepository;
    private final ReservationCreationLockManager reservationCreationLockManager;
    private final Clock clock;

    public ReservationSettingResponse getReservationSetting() {
        LocalDate startDate = LocalDate.now(clock);
        StudioSettingDetailResponse studioSetting = settingService.getStudioSetting(startDate);
        LocalDate endDate = startDate.plusDays(studioSetting.reservationOpenDays());

        return ReservationSettingResponse.from(
                studioSetting,
                settingService.getClosedDays(startDate, endDate),
                List.of()
        );
    }

    @Transactional
    public ReservationCreateResponse createReservation(ReservationCreateRequest request) {
        validateReservationTime(request.startTime(), request.endTime());
        validateAgreedTerms(request.agreedTerms());

        ShootingType shootingType = shootingTypeRepository.findActiveByCode(request.type().trim())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Shooting type not found."));

        // 락을 먼저 잡고 중복 시간 검사를 해야 동시에 들어온 요청이 함께 통과하지 않는다.
        try (ReservationCreationLockManager.ReservationLock ignored = reservationCreationLockManager.acquire(request.date())) {
            validateReservationAvailability(request.date(), request.startTime(), request.endTime());

            Reservation reservation = reservationRepository.save(Reservation.builder()
                    .shootingType(shootingType)
                    .date(request.date())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .headCount(request.headCount())
                    .name(normalizeRequiredText(request.name()))
                    .phone(normalizeRequiredText(request.phone()))
                    .visitPath(normalizeVisitPath(request.visitPath()))
                    .requestMessage(normalizeOptionalText(request.requestMessage()))
                    .build());

            return ReservationCreateResponse.from(reservation);
        }
    }

    public List<ReservedTimeResponse> getReservationsByDate(LocalDate date) {
        return reservationRepository.findBookedTimesByDate(date)
                .stream()
                .filter(Objects::nonNull)
                .map(ReservedTimeResponse::from)
                .toList();
    }

    private void validateReservationTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "endTime must be after startTime.");
        }
    }

    private void validateReservationAvailability(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 시작/종료가 살짝이라도 겹치면 동일 시간대로 본다.
        boolean overlaps = reservationRepository.existsOverlappingReservation(
                date,
                startTime,
                endTime,
                BLOCKING_STATUSES
        );

        if (overlaps) {
            throw new CustomException(
                    ErrorCode.RESERVATION_TIME_CONFLICT,
                    "Reservation time overlaps with an existing reservation."
            );
        }
    }

    private void validateAgreedTerms(List<Long> termsAgreed) {
        List<Long> agreedTerms = termsAgreed.stream()
                .distinct()
                .toList();

        List<Long> activeTermsIds = termsRepository.findActiveIdsByIdIn(agreedTerms);
        if (activeTermsIds.size() != agreedTerms.size()) {
            throw new CustomException(ErrorCode.INVALID_VALUE, "Some agreed terms were not found.");
        }

        Set<Long> agreedTermsSet = new HashSet<>(agreedTerms);
        List<Long> requiredTermsIds = termsRepository.findRequiredActiveIds();
        if (!agreedTermsSet.containsAll(requiredTermsIds)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "Required terms must be agreed.");
        }
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private VisitPath normalizeVisitPath(VisitPath visitPath) {
        return visitPath;
    }
}
