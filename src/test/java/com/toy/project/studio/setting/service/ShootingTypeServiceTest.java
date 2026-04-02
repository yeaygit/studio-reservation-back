package com.toy.project.studio.setting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ShootingTypeServiceTest {

    @Mock
    private ShootingTypeRepository shootingTypeRepository;

    @InjectMocks
    private ShootingTypeService shootingTypeService;

    @Test
    void getShootingTypesReturnsActiveShootingTypesInRepositoryOrder() {
        when(shootingTypeRepository.findAllActive()).thenReturn(List.of(
                ShootingType.builder()
                        .id(2L)
                        .code("HEADSHOT")
                        .label("Headshot")
                        .duration(30L)
                        .price(50000L)
                        .sortOrder(1L)
                        .build(),
                ShootingType.builder()
                        .id(3L)
                        .code("PROFILE")
                        .label("Profile")
                        .duration(60L)
                        .price(100000L)
                        .sortOrder(2L)
                        .build()
        ));

        List<ShootingTypeResponse> responses = shootingTypeService.getShootingTypes();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(2L);
        assertThat(responses.get(0).code()).isEqualTo("HEADSHOT");
        assertThat(responses.get(1).id()).isEqualTo(3L);
        assertThat(responses.get(1).label()).isEqualTo("Profile");
    }

    @Test
    void getShootingTypeReturnsActiveShootingType() {
        ShootingType shootingType = ShootingType.builder()
                .id(1L)
                .code("PROFILE")
                .label("Profile")
                .duration(60L)
                .price(100000L)
                .description("Main product")
                .sortOrder(1L)
                .build();

        when(shootingTypeRepository.findActiveById(1L)).thenReturn(Optional.of(shootingType));

        ShootingTypeResponse response = shootingTypeService.getShootingType(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("PROFILE");
        assertThat(response.description()).isEqualTo("Main product");
    }

    @Test
    void getShootingTypeThrowsWhenActiveShootingTypeDoesNotExist() {
        when(shootingTypeRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shootingTypeService.getShootingType(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
