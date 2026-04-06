package com.toy.project.studio.setting.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.dto.response.StudioSettingDetailResponse;
import com.toy.project.studio.setting.service.ShootingTypeService;
import com.toy.project.studio.setting.service.SettingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/settings")
public class SettingController {

    private final SettingService settingService;
    private final ShootingTypeService shootingTypeService;

//    @GetMapping("/studio")
//    public ResponseEntity<StudioSettingDetailResponse> getStudioSetting(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        return ResponseEntity.ok(settingService.getStudioSetting(startDate, endDate));
//    }

//    @GetMapping("/closed-days")
//    public ResponseEntity<List<LocalDate>> getClosedDays(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        return ResponseEntity.ok(settingService.getClosedDays(startDate, endDate));
//    }

    @GetMapping("/shooting-types")
    public ResponseEntity<List<ShootingTypeResponse>> getShootingTypes() {
        return ResponseEntity.ok(shootingTypeService.getShootingTypes());
    }

    @GetMapping("/shooting-types/{shootingTypeId}")
    public ResponseEntity<ShootingTypeResponse> getShootingType(@PathVariable Long shootingTypeId) {
        return ResponseEntity.ok(shootingTypeService.getShootingType(shootingTypeId));
    }
}
