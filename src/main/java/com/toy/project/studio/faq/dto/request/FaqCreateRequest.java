package com.toy.project.studio.faq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FaqCreateRequest(
        @NotBlank(message = "question은 필수입니다.") String question,
        @NotBlank(message = "answer는 필수입니다.") String answer
) {
}
