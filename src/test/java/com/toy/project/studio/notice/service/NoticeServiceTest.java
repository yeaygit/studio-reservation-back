package com.toy.project.studio.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.notice.dto.response.NoticeResponse;
import com.toy.project.studio.notice.entity.Notice;
import com.toy.project.studio.notice.repository.NoticeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void getNoticesReturnsActiveNotices() {
        when(noticeRepository.findAllActive()).thenReturn(List.of(
                Notice.builder().id(2L).title("Important").content("content 2").isActive(true).isImportant(true).build(),
                Notice.builder().id(1L).title("Normal").content("content 1").isActive(true).isImportant(false).build()
        ));

        List<NoticeResponse> responses = noticeService.getNotices();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(2L);
        assertThat(responses.get(0).title()).isEqualTo("Important");
        assertThat(responses.get(1).id()).isEqualTo(1L);
        assertThat(responses.get(1).content()).isEqualTo("content 1");
    }

    @Test
    void getNoticeThrowsWhenActiveNoticeDoesNotExist() {
        when(noticeRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noticeService.getNotice(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
