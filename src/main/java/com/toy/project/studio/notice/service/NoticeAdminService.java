package com.toy.project.studio.notice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.notice.dto.request.NoticeCreateRequest;
import com.toy.project.studio.notice.dto.request.NoticeUpdateRequest;
import com.toy.project.studio.notice.dto.response.NoticeResponse;
import com.toy.project.studio.notice.entity.Notice;
import com.toy.project.studio.notice.repository.NoticeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeAdminService {

    private static final String NOTICE_NOT_FOUND_MESSAGE = "Notice not found.";

    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request) {
        validatePopupPeriod(request.isPopup(), request.popupStartDate(), request.popupEndDate());

        Notice notice = noticeRepository.save(Notice.builder()
                .title(normalizeRequiredText(request.title()))
                .content(normalizeOptionalText(request.content()))
                .isPopup(request.isPopup())
                .popupStartDate(resolvePopupStartDate(request.isPopup(), request.popupStartDate()))
                .popupEndDate(resolvePopupEndDate(request.isPopup(), request.popupEndDate()))
                .build());

        return NoticeResponse.from(notice);
    }

    public List<NoticeResponse> getNotices() {
        return noticeRepository.findAllActive().stream()
                .map(NoticeResponse::from)
                .toList();
    }

    public NoticeResponse getNotice(Long noticeId) {
        return NoticeResponse.from(findNotice(noticeId));
    }

    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeUpdateRequest request) {
        validatePopupPeriod(request.isPopup(), request.popupStartDate(), request.popupEndDate());

        Notice notice = findNotice(noticeId);
        notice.update(
                normalizeRequiredText(request.title()),
                normalizeOptionalText(request.content()),
                request.isPopup(),
                resolvePopupStartDate(request.isPopup(), request.popupStartDate()),
                resolvePopupEndDate(request.isPopup(), request.popupEndDate())
        );

        return NoticeResponse.from(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        findNotice(noticeId).deactivate();
    }

    private Notice findNotice(Long noticeId) {
        return noticeRepository.findActiveById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, NOTICE_NOT_FOUND_MESSAGE));
    }

    private void validatePopupPeriod(boolean isPopup, LocalDate popupStartDate, LocalDate popupEndDate) {
        if (!isPopup) {
            return;
        }

        if (popupStartDate == null || popupEndDate == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                    "Popup start and end dates are required when popup is enabled.");
        }

        if (popupEndDate.isBefore(popupStartDate)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                    "Popup end date must be on or after popup start date.");
        }
    }

    private LocalDate resolvePopupStartDate(boolean isPopup, LocalDate popupStartDate) {
        return isPopup ? popupStartDate : null;
    }

    private LocalDate resolvePopupEndDate(boolean isPopup, LocalDate popupEndDate) {
        return isPopup ? popupEndDate : null;
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }
}
