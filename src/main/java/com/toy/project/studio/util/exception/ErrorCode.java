package com.toy.project.studio.util.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-002", "입력값이 올바르지 않습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "COMMON-003", "요청 본문이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증이 필요합니다."),
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-002", "아이디 또는 비밀번호를 확인해주세요."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "유효하지 않은 access token입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "만료된 access token입니다."),
    INVALID_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "AUTH-005", "access token 자리에는 refresh token을 사용할 수 없습니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH-006", "refresh token cookie가 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-007", "유효하지 않은 refresh token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-008", "만료된 refresh token입니다."),
    INVALID_REFRESH_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "AUTH-009", "refresh token 자리에는 access token을 사용할 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND_IN_STORE(HttpStatus.UNAUTHORIZED, "AUTH-010", "Redis에 refresh token 세션이 없습니다."),
    REFRESH_TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "AUTH-011", "이미 회전된 refresh token 재사용이 감지되었습니다. 다시 로그인해주세요."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-012", "접근 권한이 없습니다."),
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "FAQ-001", "FAQ를 찾을 수 없습니다."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "RESERVATION-001", "Reservation time overlaps with an existing reservation."),
    RESERVATION_REQUEST_IN_PROGRESS(HttpStatus.CONFLICT, "RESERVATION-002", "Another reservation request for the same date is already being processed."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-003", "서버 오류입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
