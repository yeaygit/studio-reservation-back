package com.toy.project.studio.setting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toy.project.studio.setting.dto.request.ShootingTypeCreateRequest;
import com.toy.project.studio.setting.dto.request.ShootingTypeUpdateRequest;
import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShootingTypeAdminService {

    private final ShootingTypeRepository shootingTypeRepository;

    @Transactional
    public ShootingTypeResponse createShootingType(ShootingTypeCreateRequest request) {
        String code = normalizeRequiredText(request.code());
        validateDuplicateCode(code);

        ShootingType shootingType = shootingTypeRepository.save(ShootingType.builder()
                .code(code)
                .label(normalizeRequiredText(request.label()))
                .duration(request.duration())
                .price(request.price())
                .description(normalizeOptionalText(request.description()))
                .sortOrder(resolveSortOrder())
                .build());

        return ShootingTypeResponse.from(shootingType);
    }

    public List<ShootingTypeResponse> getShootingTypes() {
        return shootingTypeRepository.findAllActive().stream()
                .map(ShootingTypeResponse::from)
                .toList();
    }

    public ShootingTypeResponse getShootingType(Long shootingTypeId) {
        return ShootingTypeResponse.from(findActiveShootingType(shootingTypeId));
    }

    @Transactional
    public ShootingTypeResponse updateShootingType(Long shootingTypeId, ShootingTypeUpdateRequest request) {
        ShootingType shootingType = findActiveShootingType(shootingTypeId);
        shootingType.update(
                normalizeRequiredText(request.label()),
                request.duration(),
                request.price(),
                normalizeOptionalText(request.description()),
                request.sortOrder()
        );

        return ShootingTypeResponse.from(shootingType);
    }

    @Transactional
    public void deleteShootingType(Long shootingTypeId) {
        findActiveShootingType(shootingTypeId).deactivate();
    }

    private Long resolveSortOrder() {
        return shootingTypeRepository.findMaxSortOrder() + 1;
    }

    private void validateDuplicateCode(String code) {
        if (shootingTypeRepository.existsActiveByCode(code)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "Shooting type code already exists.");
        }
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    private ShootingType findActiveShootingType(Long shootingTypeId) {
        return shootingTypeRepository.findActiveById(shootingTypeId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VALUE, "Shooting type not found."));
    }
}
