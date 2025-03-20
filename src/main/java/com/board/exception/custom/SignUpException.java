package com.board.exception.custom;

import com.board.exception.ErrorCode;
import lombok.Getter;

@Getter
public class SignUpException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    private SignUpException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static SignUpException from(String errorMessage) {
        return new SignUpException(ErrorCode.DUPLICATE, errorMessage);
    }
}
