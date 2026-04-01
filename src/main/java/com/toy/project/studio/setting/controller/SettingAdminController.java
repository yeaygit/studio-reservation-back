package com.toy.project.studio.setting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.project.studio.setting.dto.request.StudioSettingUpdateRequest;
import com.toy.project.studio.setting.dto.response.StudioSettingResponse;
import com.toy.project.studio.setting.service.SettingAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/settings")
public class SettingAdminController {

    private final SettingAdminService settingAdminService;

    // 현재 스튜디오 운영 설정 1건을 조회한다.
    @GetMapping("/studio")
    public ResponseEntity<StudioSettingResponse> getStudioSetting() {
        return ResponseEntity.ok(settingAdminService.getStudioSetting());
    }

    // 운영 시간, 점심 시간, 예약 단위, 요일별 휴무 여부를 한 번에 수정한다.
    @PatchMapping("/studio")
    public ResponseEntity<StudioSettingResponse> updateStudioSetting(
            @Valid @RequestBody StudioSettingUpdateRequest request
    ) {
        return ResponseEntity.ok(settingAdminService.updateStudioSetting(request));
    }
}
