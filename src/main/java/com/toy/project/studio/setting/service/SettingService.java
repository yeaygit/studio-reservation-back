package com.toy.project.studio.setting.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;
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
public class SettingService {

    private final StudioSettingRepository studioSettingRepository;
    private final ClosedDayRepository closedDayRepository;

    public StudioSettingDetailResponse getStudioSetting(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return StudioSettingDetailResponse.from(findStudioSetting(), startDate, endDate);
    }

    public StudioSettingDetailResponse getStudioSetting(LocalDate startDate) {
        StudioSetting studioSetting = findStudioSetting();
        LocalDate endDate = startDate.plusDays(studioSetting.getReservationOpenDays());
        validateDateRange(startDate, endDate);
        return StudioSettingDetailResponse.from(studioSetting, startDate, endDate);
    }

    public List<LocalDate> getClosedDays(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        // 휴일
        return closedDayRepository.findAllActiveByDateRange(
                        startDate,
                        endDate,
                        ClosedDayType.SPECIFIC,
                        ClosedDayType.ANNUAL
                ).stream()
                .flatMap(closedDay -> resolveClosedDates(closedDay, startDate, endDate).stream())
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private StudioSetting findStudioSetting() {
        return studioSettingRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new CustomException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Studio setting not found."
                ));
    }

    private List<LocalDate> resolveClosedDates(ClosedDay closedDay, LocalDate startDate, LocalDate endDate) {
        if (closedDay.getType() == ClosedDayType.SPECIFIC) {
            LocalDate specificDate = closedDay.getSpecificDate();
            if (specificDate == null || specificDate.isBefore(startDate) || specificDate.isAfter(endDate)) {
                return List.of();
            }
            return List.of(specificDate);
        }

        return IntStream.rangeClosed(startDate.getYear(), endDate.getYear())
                .mapToObj(year -> resolveAnnualClosedDate(closedDay, year))
                .filter(Objects::nonNull)
                .filter(closedDate -> !closedDate.isBefore(startDate) && !closedDate.isAfter(endDate))
                .toList();
    }

    private LocalDate resolveAnnualClosedDate(ClosedDay closedDay, int year) {
        if (closedDay.getAnnualMonth() == null || closedDay.getAnnualDay() == null) {
            return null;
        }

        try {
            return MonthDay.of(closedDay.getAnnualMonth(), closedDay.getAnnualDay()).atYear(year);
        } catch (DateTimeException exception) {
            return null;
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "startDate must be on or before endDate.");
        }
    }
}
