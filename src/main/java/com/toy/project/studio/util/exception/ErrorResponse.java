package com.toy.project.studio.util.exception;

public record ErrorResponse(
        String errorCode,
        String errorMessage
) {

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage) {
        return new ErrorResponse(errorCode.getErrorCode(), errorMessage);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getErrorMessage());
    }
}
