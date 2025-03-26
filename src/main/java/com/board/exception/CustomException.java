package com.board.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorType exceptionType;

    public CustomException(ErrorType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public Map<String, Object> getAdditionalDetails() {
        return Map.of();
    }
}
