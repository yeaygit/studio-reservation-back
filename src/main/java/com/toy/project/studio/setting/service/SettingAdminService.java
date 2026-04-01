package com.toy.project.studio.setting.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.dto.request.ClosedDayCreateRequest;
import com.toy.project.studio.setting.dto.request.StudioSettingUpdateRequest;
import com.toy.project.studio.setting.dto.response.ClosedDayResponse;
import com.toy.project.studio.setting.dto.response.StudioSettingResponse;
import com.toy.project.studio.setting.entity.ClosedDay;
import com.toy.project.studio.setting.entity.StudioSetting;
import com.toy.project.studio.setting.enumeration.ClosedDayType;
import com.toy.project.studio.setting.repository.ClosedDayRepository;
import com.toy.project.studio.setting.repository.StudioSettingRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingAdminService {

    private final StudioSettingRepository studioSettingRepository;
    private final ClosedDayRepository closedDayRepository;

    public StudioSettingResponse getStudioSetting() {
        return StudioSettingResponse.from(findStudioSetting());
    }

    @Transactional
    public StudioSettingResponse updateStudioSetting(StudioSettingUpdateRequest request) {
        StudioSetting studioSetting = findStudioSetting();
        studioSetting.update(
                request.openTime(),
                request.closeTime(),
                request.lunchStart(),
                request.lunchEnd(),
                request.slotUnit(),
                request.reservationOpenDays(),
                request.closedSun(),
                request.closedMon(),
                request.closedTue(),
                request.closedWed(),
                request.closedThu(),
                request.closedFri(),
                request.closedSat()
        );

        return StudioSettingResponse.from(studioSetting);
    }

    public List<ClosedDayResponse> getClosedDays(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.plusYears(1);

        return closedDayRepository.findAllActiveByYear(
                        startDate,
                        endDate,
                        ClosedDayType.SPECIFIC,
                        ClosedDayType.ANNUAL
                ).stream()
                .map(closedDay -> ClosedDayResponse.from(closedDay, year))
                .sorted(Comparator.comparing(
                                ClosedDayResponse::closedDate,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                        .thenComparing(ClosedDayResponse::id))
                .toList();
    }

    @Transactional
    public ClosedDayResponse createClosedDay(ClosedDayCreateRequest request) {
        validateCreateClosedDayRequest(request);

        ClosedDay closedDay = switch (request.type()) {
            case SPECIFIC -> ClosedDay.builder()
                    .type(ClosedDayType.SPECIFIC)
                    .specificDate(request.specificDate())
                    .build();
            case ANNUAL -> ClosedDay.builder()
                    .type(ClosedDayType.ANNUAL)
                    .annualMonth(request.annualMonth())
                    .annualDay(request.annualDay())
                    .build();
        };

        return ClosedDayResponse.from(closedDayRepository.save(closedDay));
    }

    @Transactional
    public void deleteClosedDay(Long closedDayId) {
        findActiveClosedDay(closedDayId).deactivate();
    }

    private void validateCreateClosedDayRequest(ClosedDayCreateRequest request) {
        switch (request.type()) {
            case SPECIFIC -> validateSpecificClosedDayRequest(request);
            case ANNUAL -> validateAnnualClosedDayRequest(request);
        }
    }

    private void validateSpecificClosedDayRequest(ClosedDayCreateRequest request) {
        if (request.specificDate() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "specificDate is required when type is SPECIFIC.");
        }
        if (request.annualMonth() != null || request.annualDay() != null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "annualMonth and annualDay must be empty when type is SPECIFIC."
            );
        }
    }

    private void validateAnnualClosedDayRequest(ClosedDayCreateRequest request) {
        if (request.specificDate() != null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "specificDate must be empty when type is ANNUAL."
            );
        }
        if (request.annualMonth() == null || request.annualDay() == null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "annualMonth and annualDay are required when type is ANNUAL."
            );
        }

        try {
            MonthDay.of(request.annualMonth(), request.annualDay());
        } catch (DateTimeException exception) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "annualMonth and annualDay do not form a valid calendar date."
            );
        }
    }

    private ClosedDay findActiveClosedDay(Long closedDayId) {
        return closedDayRepository.findActiveById(closedDayId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Closed day not found."));
    }

    private StudioSetting findStudioSetting() {
        return studioSettingRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new CustomException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Studio setting not found."
                ));
    }
}
