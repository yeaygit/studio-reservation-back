package com.toy.project.studio.setting.support;

import java.time.LocalTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.entity.StudioSetting;
import com.toy.project.studio.setting.repository.StudioSettingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudioSettingInitializer implements ApplicationRunner {

    private final StudioSettingRepository studioSettingRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (studioSettingRepository.count() > 0) {
            return;
        }

        studioSettingRepository.save(StudioSetting.builder()
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(19, 0))
                .lunchStart(LocalTime.of(13, 0))
                .lunchEnd(LocalTime.of(14, 0))
                .slotUnit(30)
                .reservationOpenDays(30)
                .closedSun(false)
                .closedMon(false)
                .closedTue(false)
                .closedWed(false)
                .closedThu(false)
                .closedFri(false)
                .closedSat(false)
                .build());
    }
}
