package com.toy.project.studio.terms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.service.TermsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/terms")
public class TermsController {

    private final TermsService termsService;

    @GetMapping
    public ResponseEntity<List<TermsResponse>> getTerms() {
        return ResponseEntity.ok(termsService.getTerms());
    }

    @GetMapping("/{termsId}")
    public ResponseEntity<TermsResponse> getTerms(@PathVariable Long termsId) {
        return ResponseEntity.ok(termsService.getTerms(termsId));
    }
}
