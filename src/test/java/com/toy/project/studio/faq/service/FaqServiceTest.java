package com.toy.project.studio.faq.service;

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

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.entity.Faq;
import com.toy.project.studio.faq.repository.FaqRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqService faqService;

    @Test
    void getFaQsReturnsActiveFaqsFromRepositoryOrder() {
        when(faqRepository.findAllActive()).thenReturn(List.of(
                Faq.builder().id(2L).question("q2").answer("a2").sortOrder(1).build(),
                Faq.builder().id(3L).question("q3").answer("a3").sortOrder(2).build()
        ));

        List<FaqResponse> responses = faqService.getFaqs();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(2L);
        assertThat(responses.get(0).question()).isEqualTo("q2");
        assertThat(responses.get(1).id()).isEqualTo(3L);
        assertThat(responses.get(1).answer()).isEqualTo("a3");
    }

}
