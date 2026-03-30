package com.toy.project.studio.faq.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.toy.project.studio.faq.dto.response.FaqResponse;
import com.toy.project.studio.faq.dto.request.FaqCreateRequest;
import com.toy.project.studio.faq.dto.request.FaqUpdateRequest;
import com.toy.project.studio.faq.service.FaqAdminService;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/faq")
public class FaqAdminController {

    private final FaqAdminService faqAdminService;

    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(@Valid @RequestBody FaqCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(faqAdminService.createFaq(request));
    }

    @GetMapping
    public ResponseEntity<List<FaqResponse>> getFaqs() {
        return ResponseEntity.ok(faqAdminService.getFaqs());
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<FaqResponse> getFaq(@PathVariable Long faqId) {
        return ResponseEntity.ok(faqAdminService.getFaq(faqId));
    }

    @PatchMapping("/{faqId}")
    public ResponseEntity<FaqResponse> updateFaq(
            @PathVariable Long faqId,
            @Valid @RequestBody FaqUpdateRequest request
    ) {
        return ResponseEntity.ok(faqAdminService.updateFaq(faqId, request));
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long faqId) {
        faqAdminService.deleteFaq(faqId);
        return ResponseEntity.noContent().build();
    }
}
