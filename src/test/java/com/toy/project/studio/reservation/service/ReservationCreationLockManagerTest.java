package com.toy.project.studio.reservation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ReservationCreationLockManagerTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void acquireReturnsLockAndReleasesItOnClose() {
        ReservationCreationLockManager lockManager = new ReservationCreationLockManager(
                stringRedisTemplate,
                Duration.ofSeconds(10),
                Duration.ZERO,
                Duration.ZERO
        );
        LocalDate date = LocalDate.of(2026, 4, 10);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(
                eq("reservation:create:2026-04-10"),
                anyString(),
                eq(Duration.ofSeconds(10))
        )).thenReturn(true);

        ReservationCreationLockManager.ReservationLock lock = lockManager.acquire(date);
        lock.close();

        verify(stringRedisTemplate).execute(
                org.mockito.ArgumentMatchers.<DefaultRedisScript<Long>>any(),
                eq(List.of("reservation:create:2026-04-10")),
                anyString()
        );
    }

    @Test
    void acquireThrowsWhenDateLockCannotBeAcquired() {
        ReservationCreationLockManager lockManager = new ReservationCreationLockManager(
                stringRedisTemplate,
                Duration.ofSeconds(10),
                Duration.ZERO,
                Duration.ZERO
        );
        LocalDate date = LocalDate.of(2026, 4, 10);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(
                eq("reservation:create:2026-04-10"),
                anyString(),
                eq(Duration.ofSeconds(10))
        )).thenReturn(false);

        assertThatThrownBy(() -> lockManager.acquire(date))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getErrorCode())
                .isEqualTo(ErrorCode.RESERVATION_REQUEST_IN_PROGRESS);
    }
}
