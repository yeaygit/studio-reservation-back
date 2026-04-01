package com.toy.project.studio.setting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.toy.project.studio.setting.dto.request.ClosedDayCreateRequest;
import com.toy.project.studio.setting.dto.response.ClosedDayResponse;
import com.toy.project.studio.setting.entity.ClosedDay;
import com.toy.project.studio.setting.enumeration.ClosedDayType;
import com.toy.project.studio.setting.repository.ClosedDayRepository;
import com.toy.project.studio.setting.repository.StudioSettingRepository;
import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class SettingAdminServiceTest {

    @Mock
    private StudioSettingRepository studioSettingRepository;

    @Mock
    private ClosedDayRepository closedDayRepository;

    @InjectMocks
    private SettingAdminService settingAdminService;

    @Captor
    private ArgumentCaptor<ClosedDay> closedDayCaptor;

    @Test
    void getClosedDaysReturnsDatesResolvedForRequestedYear() {
        ClosedDay specificClosedDay = ClosedDay.builder()
                .id(2L)
                .type(ClosedDayType.SPECIFIC)
                .specificDate(LocalDate.of(2026, 5, 5))
                .build();
        ClosedDay annualClosedDay = ClosedDay.builder()
                .id(1L)
                .type(ClosedDayType.ANNUAL)
                .annualMonth(1)
                .annualDay(1)
                .build();

        when(closedDayRepository.findAllActiveByYear(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                ClosedDayType.SPECIFIC,
                ClosedDayType.ANNUAL
        )).thenReturn(List.of(specificClosedDay, annualClosedDay));

        List<ClosedDayResponse> responses = settingAdminService.getClosedDays(2026);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ClosedDayResponse::id)
                .containsExactly(1L, 2L);
        assertThat(responses).extracting(ClosedDayResponse::closedDate)
                .containsExactly(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 5));
        assertThat(responses.get(0).annualMonth()).isEqualTo(1);
        assertThat(responses.get(0).annualDay()).isEqualTo(1);
        assertThat(responses.get(1).specificDate()).isEqualTo(LocalDate.of(2026, 5, 5));
    }

    @Test
    void getClosedDaysDoesNotThrowWhenLegacyAnnualDataIsInvalid() {
        ClosedDay invalidAnnualClosedDay = ClosedDay.builder()
                .id(3L)
                .type(ClosedDayType.ANNUAL)
                .annualMonth(13)
                .annualDay(1)
                .build();
        ClosedDay validSpecificClosedDay = ClosedDay.builder()
                .id(1L)
                .type(ClosedDayType.SPECIFIC)
                .specificDate(LocalDate.of(2026, 3, 1))
                .build();

        when(closedDayRepository.findAllActiveByYear(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                ClosedDayType.SPECIFIC,
                ClosedDayType.ANNUAL
        )).thenReturn(List.of(invalidAnnualClosedDay, validSpecificClosedDay));

        List<ClosedDayResponse> responses = settingAdminService.getClosedDays(2026);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).closedDate()).isEqualTo(LocalDate.of(2026, 3, 1));
        assertThat(responses.get(1).id()).isEqualTo(3L);
        assertThat(responses.get(1).closedDate()).isNull();
        assertThat(responses.get(1).annualMonth()).isEqualTo(13);
        assertThat(responses.get(1).annualDay()).isEqualTo(1);
    }

    @Test
    void createSpecificClosedDaySavesSpecificDateOnly() {
        ClosedDayCreateRequest request = new ClosedDayCreateRequest(
                ClosedDayType.SPECIFIC,
                LocalDate.of(2026, 12, 25),
                null,
                null
        );

        when(closedDayRepository.save(any(ClosedDay.class))).thenAnswer(invocation -> {
            ClosedDay closedDay = invocation.getArgument(0, ClosedDay.class);
            return ClosedDay.builder()
                    .id(1L)
                    .type(closedDay.getType())
                    .specificDate(closedDay.getSpecificDate())
                    .annualMonth(closedDay.getAnnualMonth())
                    .annualDay(closedDay.getAnnualDay())
                    .isActive(closedDay.isActive())
                    .build();
        });

        ClosedDayResponse response = settingAdminService.createClosedDay(request);

        verify(closedDayRepository).save(closedDayCaptor.capture());
        assertThat(closedDayCaptor.getValue().getType()).isEqualTo(ClosedDayType.SPECIFIC);
        assertThat(closedDayCaptor.getValue().getSpecificDate()).isEqualTo(LocalDate.of(2026, 12, 25));
        assertThat(closedDayCaptor.getValue().getAnnualMonth()).isNull();
        assertThat(closedDayCaptor.getValue().getAnnualDay()).isNull();
        assertThat(closedDayCaptor.getValue().isActive()).isTrue();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.closedDate()).isEqualTo(LocalDate.of(2026, 12, 25));
    }

    @Test
    void createAnnualClosedDaySavesMonthAndDayOnly() {
        ClosedDayCreateRequest request = new ClosedDayCreateRequest(
                ClosedDayType.ANNUAL,
                null,
                12,
                31
        );

        when(closedDayRepository.save(any(ClosedDay.class))).thenAnswer(invocation -> {
            ClosedDay closedDay = invocation.getArgument(0, ClosedDay.class);
            return ClosedDay.builder()
                    .id(1L)
                    .type(closedDay.getType())
                    .specificDate(closedDay.getSpecificDate())
                    .annualMonth(closedDay.getAnnualMonth())
                    .annualDay(closedDay.getAnnualDay())
                    .isActive(closedDay.isActive())
                    .build();
        });

        ClosedDayResponse response = settingAdminService.createClosedDay(request);

        verify(closedDayRepository).save(closedDayCaptor.capture());
        assertThat(closedDayCaptor.getValue().getType()).isEqualTo(ClosedDayType.ANNUAL);
        assertThat(closedDayCaptor.getValue().getSpecificDate()).isNull();
        assertThat(closedDayCaptor.getValue().getAnnualMonth()).isEqualTo(12);
        assertThat(closedDayCaptor.getValue().getAnnualDay()).isEqualTo(31);
        assertThat(closedDayCaptor.getValue().isActive()).isTrue();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.closedDate()).isNull();
        assertThat(response.annualMonth()).isEqualTo(12);
        assertThat(response.annualDay()).isEqualTo(31);
    }

    @Test
    void createClosedDayThrowsWhenSpecificRequestContainsAnnualFields() {
        ClosedDayCreateRequest request = new ClosedDayCreateRequest(
                ClosedDayType.SPECIFIC,
                LocalDate.of(2026, 1, 1),
                1,
                1
        );

        assertThatThrownBy(() -> settingAdminService.createClosedDay(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    void deleteClosedDayMarksClosedDayInactive() {
        ClosedDay closedDay = ClosedDay.builder()
                .id(1L)
                .type(ClosedDayType.SPECIFIC)
                .specificDate(LocalDate.of(2026, 1, 1))
                .build();

        when(closedDayRepository.findActiveById(1L)).thenReturn(Optional.of(closedDay));

        settingAdminService.deleteClosedDay(1L);

        assertThat(closedDay.isActive()).isFalse();
    }

    @Test
    void deleteClosedDayThrowsWhenActiveClosedDayDoesNotExist() {
        when(closedDayRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingAdminService.deleteClosedDay(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_VALUE);
    }
}
