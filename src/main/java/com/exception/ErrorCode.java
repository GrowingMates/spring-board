package com.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "엔티티를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("VALIDATION_ERROR", "유효성 검증 실패", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("AUTHENTICATION_ERROR", "인증 실패", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("AUTHORIZATION_ERROR", "권한 없음", HttpStatus.FORBIDDEN),
    SYSTEM_ERROR("SYSTEM_ERROR", "시스템 오류", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
