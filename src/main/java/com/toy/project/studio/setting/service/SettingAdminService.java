package com.toy.project.studio.setting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.dto.request.StudioSettingUpdateRequest;
import com.toy.project.studio.setting.dto.response.StudioSettingResponse;
import com.toy.project.studio.setting.entity.StudioSetting;
import com.toy.project.studio.setting.repository.StudioSettingRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingAdminService {

    private final StudioSettingRepository studioSettingRepository;

    public StudioSettingResponse getStudioSetting() {
        return StudioSettingResponse.from(findStudioSetting());
    }

    @Transactional
    public StudioSettingResponse updateStudioSetting(StudioSettingUpdateRequest request) {
        StudioSetting studioSetting = findStudioSetting();
        studioSetting.update(
                request.openTime(),
                request.closeTime(),
                request.lunchStart(),
                request.lunchEnd(),
                request.slotUnit(),
                request.reservationOpenDays(),
                request.closedSun(),
                request.closedMon(),
                request.closedTue(),
                request.closedWed(),
                request.closedThu(),
                request.closedFri(),
                request.closedSat()
        );

        return StudioSettingResponse.from(studioSetting);
    }

    private StudioSetting findStudioSetting() {
        return studioSettingRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new CustomException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "스튜디오 기본 설정이 존재하지 않습니다."
                ));
    }
}
