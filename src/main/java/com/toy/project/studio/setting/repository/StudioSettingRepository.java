package com.toy.project.studio.setting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.project.studio.setting.entity.StudioSetting;

public interface StudioSettingRepository extends JpaRepository<StudioSetting, Long> {

    Optional<StudioSetting> findFirstByOrderByIdAsc();
}
