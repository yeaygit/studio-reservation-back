package com.toy.project.studio.terms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.terms.dto.request.TermsCreateRequest;
import com.toy.project.studio.terms.dto.request.TermsUpdateRequest;
import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.entity.Terms;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsAdminService {

    private final TermsRepository termsRepository;

    @Transactional
    public TermsResponse createTerms(TermsCreateRequest request) {
        Terms terms = termsRepository.save(Terms.builder()
                .title(normalizeRequiredText(request.title()))
                .content(normalizeRequiredText(request.content()))
                .isRequired(request.isRequired())
                .build());

        return TermsResponse.from(terms);
    }

    public List<TermsResponse> getTerms() {
        return termsRepository.findAllActive().stream()
                .map(TermsResponse::from)
                .toList();
    }

    public TermsResponse getTerms(Long termsId) {
        return TermsResponse.from(findTerms(termsId));
    }

    @Transactional
    public TermsResponse updateTerms(Long termsId, TermsUpdateRequest request) {
        Terms terms = findTerms(termsId);
        terms.update(
                normalizeRequiredText(request.title()),
                normalizeRequiredText(request.content()),
                request.isRequired()
        );

        return TermsResponse.from(terms);
    }

    @Transactional
    public void deleteTerms(Long termsId) {
        findTerms(termsId).deactivate();
    }

    private Terms findTerms(Long termsId) {
        return termsRepository.findActiveById(termsId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Terms not found."));
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }
}
