package com.toy.project.studio.faq.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FaqUpdateRequest(
        @NotBlank(message = "question은 필수입니다.") String question,
        @NotBlank(message = "answer는 필수입니다.") String answer,
        @NotNull(message = "sortOrder는 필수입니다.") Integer sortOrder
) {
}
