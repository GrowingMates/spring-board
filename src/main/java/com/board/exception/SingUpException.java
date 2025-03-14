package com.board.exception;

import lombok.Getter;

@Getter
public class SingUpException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    private SingUpException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static SingUpException from(String errorMessage) {
        return new SingUpException(ErrorCode.DUPLICATE, errorMessage);
    }
}
