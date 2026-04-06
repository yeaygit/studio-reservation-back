package com.toy.project.studio.notice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.toy.project.studio.notice.entity.Notice;

public record NoticeResponse(
        Long id,
        String title,
        String content,
        boolean isPopup,
        LocalDate popupStartDate,
        LocalDate popupEndDate,
        String createdAt
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPopup(),
                notice.getPopupStartDate(),
                notice.getPopupEndDate(),
                formatDateTime(notice.getCreatedAt())
        );
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }
}
