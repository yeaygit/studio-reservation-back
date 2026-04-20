package com.toy.project.studio.reservation.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.toy.project.studio.util.exception.CustomException;
import com.toy.project.studio.util.exception.ErrorCode;

@Component
public class ReservationCreationLockManager {

    private static final String LOCK_KEY_PREFIX = "reservation:create:";
    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = createReleaseLockScript();

    private final StringRedisTemplate stringRedisTemplate;
    private final Duration lockTtl;
    private final Duration waitTimeout;
    private final Duration retryInterval;

    public ReservationCreationLockManager(
            StringRedisTemplate stringRedisTemplate,
            @Value("${reservation.lock.ttl:10s}") Duration lockTtl,
            @Value("${reservation.lock.wait-timeout:3s}") Duration waitTimeout,
            @Value("${reservation.lock.retry-interval:100ms}") Duration retryInterval
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockTtl = lockTtl;
        this.waitTimeout = waitTimeout;
        this.retryInterval = retryInterval;
    }

    public ReservationLock acquire(LocalDate date) {
        String key = buildKey(date);
        String token = UUID.randomUUID().toString();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        long deadlineNanos = System.nanoTime() + waitTimeout.toNanos();

        // 같은 날짜 예약 생성 요청은 짧은 시간 동안 하나씩만 통과시킨다.
        while (true) {
            Boolean acquired = valueOperations.setIfAbsent(key, token, lockTtl);
            if (Boolean.TRUE.equals(acquired)) {
                return () -> release(key, token);
            }

            if (System.nanoTime() >= deadlineNanos) {
                break;
            }

            waitForRetry();
        }

        throw new CustomException(
                ErrorCode.RESERVATION_REQUEST_IN_PROGRESS,
                "Another reservation request for the same date is already being processed."
        );
    }

    private void release(String key, String token) {
        // 내가 획득한 락일 때만 삭제해서 다른 요청의 락을 건드리지 않도록 한다.
        stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, List.of(key), token);
    }

    private void waitForRetry() {
        try {
            Thread.sleep(retryInterval.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "Interrupted while waiting for reservation lock.");
        }
    }

    private String buildKey(LocalDate date) {
        return LOCK_KEY_PREFIX + date;
    }

    private static DefaultRedisScript<Long> createReleaseLockScript() {
        DefaultRedisScript<Long> releaseLockScript = new DefaultRedisScript<>();
        releaseLockScript.setScriptText("""
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                end
                return 0
                """);
        releaseLockScript.setResultType(Long.class);
        return releaseLockScript;
    }

    public interface ReservationLock extends AutoCloseable {
        @Override
        void close();
    }
}
