package com.toy.project.studio.faq.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.service.FaqService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/faq")
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> getFaqs() {
        return ResponseEntity.ok(faqService.getFaqs());
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<FaqResponse> getFaq(@PathVariable Long faqId) {
        return ResponseEntity.ok(faqService.getFaq(faqId));
    }
}
