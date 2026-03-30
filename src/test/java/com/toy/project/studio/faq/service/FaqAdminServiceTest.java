package com.toy.project.studio.faq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.dto.request.FaqCreateRequest;
import com.toy.project.studio.faq.dto.request.FaqUpdateRequest;
import com.toy.project.studio.faq.entity.Faq;
import com.toy.project.studio.faq.repository.FaqRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class FaqAdminServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqAdminService faqAdminService;

    @Captor
    private ArgumentCaptor<Faq> faqCaptor;

    @Test
    void createFaQAssignsNextSortOrderAndReturnsSavedFaq() {
        FaqCreateRequest request = new FaqCreateRequest("  question  ", "  answer  ");

        when(faqRepository.findMaxSortOrder()).thenReturn(5);

        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> {
            Faq faq = invocation.getArgument(0, Faq.class);
            return Faq.builder()
                    .id(1L)
                    .question(faq.getQuestion())
                    .answer(faq.getAnswer())
                    .sortOrder(faq.getSortOrder())
                    .isActive(faq.isActive())
                    .build();
        });

        FaqResponse response = faqAdminService.createFaq(request);

        verify(faqRepository).save(faqCaptor.capture());
        assertThat(faqCaptor.getValue().getQuestion()).isEqualTo("question");
        assertThat(faqCaptor.getValue().getAnswer()).isEqualTo("answer");
        assertThat(faqCaptor.getValue().getSortOrder()).isEqualTo(6);
        assertThat(faqCaptor.getValue().isActive()).isTrue();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.question()).isEqualTo("question");
        assertThat(response.answer()).isEqualTo("answer");
        assertThat(response.sortOrder()).isEqualTo(6);
    }

    @Test
    void createFaQStartsSortOrderAtOneWhenThereIsNoFaq() {
        FaqCreateRequest request = new FaqCreateRequest("question", "answer");

        when(faqRepository.findMaxSortOrder()).thenReturn(0);
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> invocation.getArgument(0, Faq.class));

        FaqResponse response = faqAdminService.createFaq(request);

        assertThat(response.sortOrder()).isEqualTo(1);
    }

    @Test
    void updateFaQUpdatesOnlyActiveFaq() {
        Faq faq = Faq.builder()
                .id(1L)
                .question("before")
                .answer("before answer")
                .sortOrder(1)
                .build();
        FaqUpdateRequest request = new FaqUpdateRequest("  after  ", "  after answer  ", 2);

        when(faqRepository.findActiveById(1L)).thenReturn(Optional.of(faq));

        FaqResponse response = faqAdminService.updateFaq(1L, request);

        assertThat(faq.getQuestion()).isEqualTo("after");
        assertThat(faq.getAnswer()).isEqualTo("after answer");
        assertThat(faq.getSortOrder()).isEqualTo(2);
        assertThat(response.question()).isEqualTo("after");
        assertThat(response.answer()).isEqualTo("after answer");
        assertThat(response.sortOrder()).isEqualTo(2);
    }

    @Test
    void deleteFaQMarksFaqInactive() {
        Faq faq = Faq.builder()
                .id(1L)
                .question("question")
                .answer("answer")
                .sortOrder(1)
                .build();

        when(faqRepository.findActiveById(1L)).thenReturn(Optional.of(faq));

        faqAdminService.deleteFaq(1L);

        assertThat(faq.isActive()).isFalse();
    }

    @Test
    void getFaQThrowsWhenActiveFaqDoesNotExist() {
        when(faqRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqAdminService.getFaq(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FAQ_NOT_FOUND);
    }
}
