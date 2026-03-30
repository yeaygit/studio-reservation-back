package com.toy.project.studio.faq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.repository.FaqRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqRepository;

    public List<FaqResponse> getFaqs() {
        return faqRepository.findAllActive().stream()
                .map(FaqResponse::from)
                .toList();
    }

    public FaqResponse getFaq(Long faqId) {
        return faqRepository.findActiveById(faqId)
                .map(FaqResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));
    }
}
