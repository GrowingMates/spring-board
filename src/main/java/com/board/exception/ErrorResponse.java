package com.board.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
class ErrorResponse {
    private int statusCode;
    private LocalDateTime timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;

    @Builder
    public ErrorResponse(int statusCode, LocalDateTime timestamp, String code, String message) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return builder()
                .statusCode(errorCode.getHttpStatus().value())
                .timestamp(LocalDateTime.now())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public void addDetail(String key, Object value) {
        if (details == null) {
            details = new HashMap<>();
        }
        this.details.put(key, value);
    }
}
