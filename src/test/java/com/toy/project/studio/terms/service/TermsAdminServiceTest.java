package com.toy.project.studio.terms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.toy.project.studio.terms.dto.request.TermsCreateRequest;
import com.toy.project.studio.terms.dto.request.TermsUpdateRequest;
import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.entity.Terms;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class TermsAdminServiceTest {

    @Mock
    private TermsRepository termsRepository;

    @InjectMocks
    private TermsAdminService termsAdminService;

    @Captor
    private ArgumentCaptor<Terms> termsCaptor;

    @Test
    void createTermsTrimsFieldsAndReturnsSavedTerms() {
        TermsCreateRequest request = new TermsCreateRequest("  Terms title  ", "  Terms content  ", true, true);

        when(termsRepository.save(any(Terms.class))).thenAnswer(invocation -> {
            Terms terms = invocation.getArgument(0, Terms.class);
            return Terms.builder()
                    .id(1L)
                    .title(terms.getTitle())
                    .content(terms.getContent())
                    .isRequired(terms.isRequired())
                    .isActive(terms.isActive())
                    .build();
        });

        TermsResponse response = termsAdminService.createTerms(request);

        verify(termsRepository).save(termsCaptor.capture());
        assertThat(termsCaptor.getValue().getTitle()).isEqualTo("Terms title");
        assertThat(termsCaptor.getValue().getContent()).isEqualTo("Terms content");
        assertThat(termsCaptor.getValue().isRequired()).isTrue();
        assertThat(termsCaptor.getValue().isActive()).isTrue();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Terms title");
        assertThat(response.content()).isEqualTo("Terms content");
    }

    @Test
    void updateTermsUpdatesExistingTerms() {
        Terms terms = Terms.builder()
                .id(1L)
                .title("before")
                .content("before content")
                .isRequired(true)
                .isActive(true)
                .build();
        TermsUpdateRequest request = new TermsUpdateRequest("  after  ", "  after content  ", false, false);

        when(termsRepository.findById(1L)).thenReturn(Optional.of(terms));

        TermsResponse response = termsAdminService.updateTerms(1L, request);

        assertThat(terms.getTitle()).isEqualTo("after");
        assertThat(terms.getContent()).isEqualTo("after content");
        assertThat(terms.isRequired()).isFalse();
        assertThat(terms.isActive()).isFalse();
        assertThat(response.title()).isEqualTo("after");
        assertThat(response.isRequired()).isFalse();
        assertThat(response.isActive()).isFalse();
    }

    @Test
    void deleteTermsMarksExistingTermsInactive() {
        Terms terms = Terms.builder()
                .id(1L)
                .title("title")
                .content("content")
                .isActive(true)
                .build();

        when(termsRepository.findById(1L)).thenReturn(Optional.of(terms));

        termsAdminService.deleteTerms(1L);

        assertThat(terms.isActive()).isFalse();
        verify(termsRepository, never()).delete(any(Terms.class));
    }

    @Test
    void getTermsThrowsWhenTermsDoesNotExist() {
        when(termsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> termsAdminService.getTerms(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
