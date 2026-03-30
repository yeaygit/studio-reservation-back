package com.toy.project.studio.faq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.dto.request.FaqCreateRequest;
import com.toy.project.studio.faq.dto.request.FaqUpdateRequest;
import com.toy.project.studio.faq.entity.Faq;
import com.toy.project.studio.faq.repository.FaqRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqAdminService {

    private final FaqRepository faqRepository;

    @Transactional
    public FaqResponse createFaq(FaqCreateRequest request) {
        int nextSortOrder = faqRepository.findMaxSortOrder() + 1;
        Faq faq = faqRepository.save(Faq.builder()
                .question(request.question().trim())
                .answer(request.answer().trim())
                .sortOrder(nextSortOrder)
                .build());

        return FaqResponse.from(faq);
    }

    public List<FaqResponse> getFaqs() {
        return faqRepository.findAllActive().stream()
                .map(FaqResponse::from)
                .toList();
    }

    public FaqResponse getFaq(Long faqId) {
        return FaqResponse.from(findActiveFaq(faqId));
    }

    @Transactional
    public FaqResponse updateFaq(Long faqId, FaqUpdateRequest request) {
        Faq faq = findActiveFaq(faqId);
        faq.update(
                request.question().trim(),
                request.answer().trim(),
                request.sortOrder()
        );

        return FaqResponse.from(faq);
    }

    @Transactional
    public void deleteFaq(Long faqId) {
        findActiveFaq(faqId).deactivate();
    }

    private Faq findActiveFaq(Long faqId) {
        return faqRepository.findActiveById(faqId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));
    }
}
