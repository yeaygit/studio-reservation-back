package com.toy.project.studio.faq.dto.response;

import com.toy.project.studio.faq.entity.Faq;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record FaqResponse(
        Long id,
        String question,
        String answer,
        Integer sortOrder,
        String createdAt
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getSortOrder(),
                formatDateTime(faq.getCreatedAt())
        );
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }
}
