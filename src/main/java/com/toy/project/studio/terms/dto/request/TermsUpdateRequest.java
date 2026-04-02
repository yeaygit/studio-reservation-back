package com.toy.project.studio.terms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TermsUpdateRequest(
        @NotBlank(message = "title is required.") @Size(max = 200, message = "title must be 200 characters or fewer.") String title,
        @NotBlank(message = "content is required.") String content,
        @NotNull(message = "isRequired is required.") Boolean isRequired
) {
}
