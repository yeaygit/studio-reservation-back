package com.toy.project.studio.terms.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.toy.project.studio.terms.entity.Terms;

public record TermsResponse(
        Long id,
        String title,
        String content,
        boolean isRequired,
        String createdAt
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static TermsResponse from(Terms terms) {
        return new TermsResponse(
                terms.getId(),
                terms.getTitle(),
                terms.getContent(),
                terms.isRequired(),
                formatDateTime(terms.getCreatedAt())
        );
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }
}
