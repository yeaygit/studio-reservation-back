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

    private final SettingService settingService;
    private final ReservationRepository reservationRepository;
    private final ShootingTypeRepository shootingTypeRepository;
    private final TermsRepository termsRepository;
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

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .shootingType(shootingType)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .headCount(request.headCount())
                .name(normalizeRequiredText(request.name()))
                .phone(normalizeRequiredText(request.phone()))
                .visitPath(normalizeOptionalText(request.visitPath()))
                .requestMessage(normalizeOptionalText(request.requestMessage()))
                .build());

        return ReservationCreateResponse.from(reservation);
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
}
