package com.toy.project.studio.setting.dto.request;

import java.time.LocalDate;

import com.toy.project.studio.setting.enumeration.ClosedDayType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ClosedDayCreateRequest(
        @NotNull(message = "type is required") ClosedDayType type,
        LocalDate specificDate,
        @Min(value = 1, message = "annualMonth must be between 1 and 12")
        @Max(value = 12, message = "annualMonth must be between 1 and 12")
        Integer annualMonth,
        @Min(value = 1, message = "annualDay must be between 1 and 31")
        @Max(value = 31, message = "annualDay must be between 1 and 31")
        Integer annualDay
) {
}
