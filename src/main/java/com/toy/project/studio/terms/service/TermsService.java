package com.toy.project.studio.terms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.entity.Terms;
import com.toy.project.studio.terms.repository.TermsRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsRepository termsRepository;

    public List<TermsResponse> getTerms() {
        return termsRepository.findAllActive().stream()
                .map(TermsResponse::from)
                .toList();
    }

    public TermsResponse getTerms(Long termsId) {
        Terms terms = termsRepository.findActiveById(termsId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Terms not found."));

        return TermsResponse.from(terms);
    }
}
