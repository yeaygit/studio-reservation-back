package com.toy.project.studio.setting.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.setting.dto.request.ClosedDayCreateRequest;
import com.toy.project.studio.setting.dto.request.ShootingTypeCreateRequest;
import com.toy.project.studio.setting.dto.request.ShootingTypeUpdateRequest;
import com.toy.project.studio.setting.dto.request.StudioSettingUpdateRequest;
import com.toy.project.studio.setting.dto.response.ClosedDayResponse;
import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.dto.response.StudioSettingResponse;
import com.toy.project.studio.setting.service.ShootingTypeAdminService;
import com.toy.project.studio.setting.service.SettingAdminService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/settings")
public class SettingAdminController {

    private final SettingAdminService settingAdminService;
    private final ShootingTypeAdminService shootingTypeAdminService;

    @GetMapping("/studio")
    public ResponseEntity<StudioSettingResponse> getStudioSetting() {
        return ResponseEntity.ok(settingAdminService.getStudioSetting());
    }

    @PatchMapping("/studio")
    public ResponseEntity<StudioSettingResponse> updateStudioSetting(
            @Valid @RequestBody StudioSettingUpdateRequest request
    ) {
        return ResponseEntity.ok(settingAdminService.updateStudioSetting(request));
    }

    @GetMapping("/closed-days")
    public ResponseEntity<List<ClosedDayResponse>> getClosedDays(@RequestParam Integer year) {
        return ResponseEntity.ok(settingAdminService.getClosedDays(year));
    }

    @PostMapping("/closed-days")
    public ResponseEntity<ClosedDayResponse> createClosedDay(
            @Valid @RequestBody ClosedDayCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(settingAdminService.createClosedDay(request));
    }

    @DeleteMapping("/closed-days/{closedDayId}")
    public ResponseEntity<Void> deleteClosedDay(@PathVariable Long closedDayId) {
        settingAdminService.deleteClosedDay(closedDayId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shooting-types")
    public ResponseEntity<ShootingTypeResponse> createShootingType(
            @Valid @RequestBody ShootingTypeCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shootingTypeAdminService.createShootingType(request));
    }

    @GetMapping("/shooting-types")
    public ResponseEntity<List<ShootingTypeResponse>> getShootingTypes() {
        return ResponseEntity.ok(shootingTypeAdminService.getShootingTypes());
    }

    @GetMapping("/shooting-types/{shootingTypeId}")
    public ResponseEntity<ShootingTypeResponse> getShootingType(@PathVariable Long shootingTypeId) {
        return ResponseEntity.ok(shootingTypeAdminService.getShootingType(shootingTypeId));
    }

    @PatchMapping("/shooting-types/{shootingTypeId}")
    public ResponseEntity<ShootingTypeResponse> updateShootingType(
            @PathVariable Long shootingTypeId,
            @Valid @RequestBody ShootingTypeUpdateRequest request
    ) {
        return ResponseEntity.ok(shootingTypeAdminService.updateShootingType(shootingTypeId, request));
    }

    @DeleteMapping("/shooting-types/{shootingTypeId}")
    public ResponseEntity<Void> deleteShootingType(@PathVariable Long shootingTypeId) {
        shootingTypeAdminService.deleteShootingType(shootingTypeId);
        return ResponseEntity.noContent().build();
    }
}
