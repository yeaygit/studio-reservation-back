package com.toy.project.studio.faq.dto.response;

import com.toy.project.studio.faq.entity.Faq;

public record FaqResponse(
        Long id,
        String question,
        String answer,
        Integer sortOrder
) {

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getSortOrder()
        );
    }
}
