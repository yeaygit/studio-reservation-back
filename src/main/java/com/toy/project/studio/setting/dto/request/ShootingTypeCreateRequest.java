package com.toy.project.studio.setting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ShootingTypeCreateRequest(
        @NotBlank(message = "code is required.") String code,
        @NotBlank(message = "label is required.") String label,
        @NotNull(message = "duration is required.") @Positive(message = "duration must be greater than 0.") Long duration,
        @NotNull(message = "price is required.") @PositiveOrZero(message = "price must be 0 or greater.") Long price,
        String description
) {
}
