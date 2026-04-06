package com.toy.project.studio.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.notice.dto.response.NoticeResponse;
import com.toy.project.studio.notice.entity.Notice;
import com.toy.project.studio.notice.repository.NoticeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse> getNotices() {
        return noticeRepository.findAllActive().stream()
                .map(NoticeResponse::from)
                .toList();
    }

    public NoticeResponse getNotice(Long noticeId) {
        Notice notice = noticeRepository.findActiveById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Notice not found."));

        return NoticeResponse.from(notice);
    }
}
