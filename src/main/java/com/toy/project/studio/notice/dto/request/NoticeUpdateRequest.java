package com.toy.project.studio.notice.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoticeUpdateRequest(
        @NotBlank(message = "title is required.") @Size(max = 200, message = "title must be 200 characters or fewer.") String title,
        String content,
        @NotNull(message = "isPopup is required.") Boolean isPopup,
        LocalDate popupStartDate,
        LocalDate popupEndDate
) {
}
