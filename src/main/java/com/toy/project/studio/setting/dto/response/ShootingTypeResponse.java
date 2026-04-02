package com.toy.project.studio.setting.dto.response;

import com.toy.project.studio.setting.entity.ShootingType;

public record ShootingTypeResponse(
        Long id,
        String code,
        String label,
        Long duration,
        Long price,
        String description,
        Long sortOrder
) {

    public static ShootingTypeResponse from(ShootingType shootingType) {
        return new ShootingTypeResponse(
                shootingType.getId(),
                shootingType.getCode(),
                shootingType.getLabel(),
                shootingType.getDuration(),
                shootingType.getPrice(),
                shootingType.getDescription(),
                shootingType.getSortOrder()
        );
    }
}
