package com.toy.project.studio.terms.service;

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

import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.entity.Terms;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @Mock
    private TermsRepository termsRepository;

    @InjectMocks
    private TermsService termsService;

    @Test
    void getTermsReturnsOnlyActiveTerms() {
        when(termsRepository.findAllActive()).thenReturn(List.of(
                Terms.builder().id(2L).title("Terms 2").content("content 2").isRequired(true).isActive(true).build(),
                Terms.builder().id(1L).title("Terms 1").content("content 1").isRequired(false).isActive(true).build()
        ));

        List<TermsResponse> responses = termsService.getTerms();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(2L);
        assertThat(responses.get(0).title()).isEqualTo("Terms 2");
        assertThat(responses.get(1).id()).isEqualTo(1L);
        assertThat(responses.get(1).content()).isEqualTo("content 1");
    }

    @Test
    void getTermsDetailThrowsWhenActiveTermsDoesNotExist() {
        when(termsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> termsService.getTerms(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
