package com.toy.project.studio.setting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShootingTypeService {

    private final ShootingTypeRepository shootingTypeRepository;

    public List<ShootingTypeResponse> getShootingTypes() {
        return shootingTypeRepository.findAllActive().stream()
                .map(ShootingTypeResponse::from)
                .toList();
    }

    public ShootingTypeResponse getShootingType(Long shootingTypeId) {
        ShootingType shootingType = shootingTypeRepository.findActiveById(shootingTypeId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Shooting type not found."));

        return ShootingTypeResponse.from(shootingType);
    }
}
