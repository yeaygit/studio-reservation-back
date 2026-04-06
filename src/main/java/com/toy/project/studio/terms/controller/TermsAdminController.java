package com.toy.project.studio.terms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.terms.dto.request.TermsCreateRequest;
import com.toy.project.studio.terms.dto.request.TermsUpdateRequest;
import com.toy.project.studio.terms.dto.response.TermsResponse;
import com.toy.project.studio.terms.service.TermsAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/terms")
public class TermsAdminController {

    private final TermsAdminService termsAdminService;

    @PostMapping
    public ResponseEntity<TermsResponse> createTerms(@Valid @RequestBody TermsCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(termsAdminService.createTerms(request));
    }

    @GetMapping
    public ResponseEntity<List<TermsResponse>> getTerms() {
        return ResponseEntity.ok(termsAdminService.getTerms());
    }

    @GetMapping("/{termsId}")
    public ResponseEntity<TermsResponse> getTerms(@PathVariable Long termsId) {
        return ResponseEntity.ok(termsAdminService.getTerms(termsId));
    }

    @PatchMapping("/{termsId}")
    public ResponseEntity<TermsResponse> updateTerms(
            @PathVariable Long termsId,
            @Valid @RequestBody TermsUpdateRequest request
    ) {
        return ResponseEntity.ok(termsAdminService.updateTerms(termsId, request));
    }

    @DeleteMapping("/{termsId}")
    public ResponseEntity<Void> deleteTerms(@PathVariable Long termsId) {
        termsAdminService.deleteTerms(termsId);
        return ResponseEntity.noContent().build();
    }
}
