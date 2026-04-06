package com.toy.project.studio.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.notice.dto.request.NoticeCreateRequest;
import com.toy.project.studio.notice.dto.request.NoticeUpdateRequest;
import com.toy.project.studio.notice.dto.response.NoticeResponse;
import com.toy.project.studio.notice.entity.Notice;
import com.toy.project.studio.notice.repository.NoticeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class NoticeAdminServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private NoticeAdminService noticeAdminService;

    @Captor
    private ArgumentCaptor<Notice> noticeCaptor;

    @Test
    void createNoticeTrimsFieldsAndClearsPopupDatesWhenPopupIsDisabled() {
        NoticeCreateRequest request = new NoticeCreateRequest(
                "  Notice title  ",
                "  Notice content  ",
                true,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 3)
        );

        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> {
            Notice notice = invocation.getArgument(0, Notice.class);
            return Notice.builder()
                    .id(1L)
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .isActive(notice.isActive())
                    .isPopup(notice.isPopup())
                    .popupStartDate(notice.getPopupStartDate())
                    .popupEndDate(notice.getPopupEndDate())
                    .build();
        });

        NoticeResponse response = noticeAdminService.createNotice(request);

        verify(noticeRepository).save(noticeCaptor.capture());
        assertThat(noticeCaptor.getValue().getTitle()).isEqualTo("Notice title");
        assertThat(noticeCaptor.getValue().getContent()).isEqualTo("Notice content");
        assertThat(noticeCaptor.getValue().isPopup()).isFalse();
        assertThat(noticeCaptor.getValue().getPopupStartDate()).isNull();
        assertThat(noticeCaptor.getValue().getPopupEndDate()).isNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.popupStartDate()).isNull();
        assertThat(response.popupEndDate()).isNull();
    }

    @Test
    void createNoticeThrowsWhenPopupDatesAreMissing() {
        NoticeCreateRequest request = new NoticeCreateRequest(
                "Notice title",
                "content",
                true,
                null,
                null
        );

        assertThatThrownBy(() -> noticeAdminService.createNotice(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    void updateNoticeUpdatesExistingNotice() {
        Notice notice = Notice.builder()
                .id(1L)
                .title("before")
                .content("before content")
                .isActive(true)
                .isPopup(false)
                .build();
        NoticeUpdateRequest request = new NoticeUpdateRequest(
                "  after  ",
                "  after content  ",
                false,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 10)
        );

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        NoticeResponse response = noticeAdminService.updateNotice(1L, request);

        assertThat(notice.getTitle()).isEqualTo("after");
        assertThat(notice.getContent()).isEqualTo("after content");
        assertThat(notice.isActive()).isFalse();
        assertThat(notice.isPopup()).isTrue();
        assertThat(notice.getPopupStartDate()).isEqualTo(LocalDate.of(2026, 4, 1));
        assertThat(notice.getPopupEndDate()).isEqualTo(LocalDate.of(2026, 4, 10));
        assertThat(response.isPopup()).isTrue();
        assertThat(response.popupEndDate()).isEqualTo(LocalDate.of(2026, 4, 10));
    }

    @Test
    void deleteNoticeMarksExistingNoticeInactive() {
        Notice notice = Notice.builder()
                .id(1L)
                .title("title")
                .isActive(true)
                .build();

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        noticeAdminService.deleteNotice(1L);

        assertThat(notice.isActive()).isFalse();
        verify(noticeRepository, never()).delete(any(Notice.class));
    }

    @Test
    void getNoticeThrowsWhenNoticeDoesNotExist() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noticeAdminService.getNotice(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
