package com.toy.project.studio.setting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.setting.dto.request.ShootingTypeCreateRequest;
import com.toy.project.studio.setting.dto.request.ShootingTypeUpdateRequest;
import com.toy.project.studio.setting.dto.response.ShootingTypeResponse;
import com.toy.project.studio.setting.entity.ShootingType;
import com.toy.project.studio.setting.repository.ShootingTypeRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ShootingTypeAdminServiceTest {

    @Mock
    private ShootingTypeRepository shootingTypeRepository;

    @InjectMocks
    private ShootingTypeAdminService shootingTypeAdminService;

    @Captor
    private ArgumentCaptor<ShootingType> shootingTypeCaptor;

    @Test
    void createShootingTypeAssignsNextSortOrderWhenSortOrderIsMissing() {
        ShootingTypeCreateRequest request = new ShootingTypeCreateRequest(
                "  profile  ",
                "  Profile  ",
                60L,
                100000L,
                "  Main product  "
        );

        when(shootingTypeRepository.existsActiveByCode("profile")).thenReturn(false);
        when(shootingTypeRepository.findMaxSortOrder()).thenReturn(4L);
        when(shootingTypeRepository.save(any(ShootingType.class))).thenAnswer(invocation -> {
            ShootingType shootingType = invocation.getArgument(0, ShootingType.class);
            return ShootingType.builder()
                    .id(1L)
                    .code(shootingType.getCode())
                    .label(shootingType.getLabel())
                    .duration(shootingType.getDuration())
                    .price(shootingType.getPrice())
                    .description(shootingType.getDescription())
                    .sortOrder(shootingType.getSortOrder())
                    .isActive(shootingType.isActive())
                    .build();
        });

        ShootingTypeResponse response = shootingTypeAdminService.createShootingType(request);

        verify(shootingTypeRepository).save(shootingTypeCaptor.capture());
        assertThat(shootingTypeCaptor.getValue().getCode()).isEqualTo("profile");
        assertThat(shootingTypeCaptor.getValue().getLabel()).isEqualTo("Profile");
        assertThat(shootingTypeCaptor.getValue().getDescription()).isEqualTo("Main product");
        assertThat(shootingTypeCaptor.getValue().getSortOrder()).isEqualTo(5L);
        assertThat(shootingTypeCaptor.getValue().isActive()).isTrue();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sortOrder()).isEqualTo(5L);
    }

    @Test
    void createShootingTypeRejectsDuplicateCode() {
        ShootingTypeCreateRequest request = new ShootingTypeCreateRequest(
                "  profile  ",
                "Headshot",
                30L,
                50000L,
                null
        );

        when(shootingTypeRepository.existsActiveByCode("profile")).thenReturn(true);

        assertThatThrownBy(() -> shootingTypeAdminService.createShootingType(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(customException.getErrorMessage()).isEqualTo("Shooting type code already exists.");
                });
        verify(shootingTypeRepository, never()).save(any(ShootingType.class));
    }

    @Test
    void updateShootingTypeNormalizesDescriptionAndMutableFields() {
        ShootingType shootingType = ShootingType.builder()
                .id(1L)
                .code("OLD")
                .label("Old")
                .duration(30L)
                .price(10000L)
                .description("desc")
                .sortOrder(1L)
                .build();
        ShootingTypeUpdateRequest request = new ShootingTypeUpdateRequest(
                "  New Label  ",
                90L,
                150000L,
                "   ",
                3L
        );

        when(shootingTypeRepository.findActiveById(1L)).thenReturn(Optional.of(shootingType));

        ShootingTypeResponse response = shootingTypeAdminService.updateShootingType(1L, request);

        assertThat(shootingType.getCode()).isEqualTo("OLD");
        assertThat(shootingType.getLabel()).isEqualTo("New Label");
        assertThat(shootingType.getDuration()).isEqualTo(90L);
        assertThat(shootingType.getPrice()).isEqualTo(150000L);
        assertThat(shootingType.getDescription()).isNull();
        assertThat(shootingType.getSortOrder()).isEqualTo(3L);
        assertThat(response.code()).isEqualTo("OLD");
        assertThat(response.description()).isNull();
    }

    @Test
    void deleteShootingTypeMarksShootingTypeInactive() {
        ShootingType shootingType = ShootingType.builder()
                .id(1L)
                .code("PROFILE")
                .label("Profile")
                .duration(60L)
                .price(100000L)
                .sortOrder(1L)
                .build();

        when(shootingTypeRepository.findActiveById(1L)).thenReturn(Optional.of(shootingType));

        shootingTypeAdminService.deleteShootingType(1L);

        assertThat(shootingType.isActive()).isFalse();
    }

    @Test
    void getShootingTypeThrowsWhenActiveShootingTypeDoesNotExist() {
        when(shootingTypeRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shootingTypeAdminService.getShootingType(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
