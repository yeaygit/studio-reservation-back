package com.toy.project.studio.faq.dto.response;

import com.toy.project.studio.faq.entity.Faq;

import java.time.format.DateTimeFormatter;

public record FaqResponse(
        Long id,
        String question,
        String answer,
        Integer sortOrder,
        String createdAt
) {

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getSortOrder(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(faq.getCreatedAt())
        );
    }
}
