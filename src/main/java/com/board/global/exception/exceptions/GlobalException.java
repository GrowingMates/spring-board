package com.board.global.exception.exceptions;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final GlobalErrorCode errorCode;

    public GlobalException(GlobalErrorCode errorCode) {
        super(errorCode.getCustomCode() + ": " + errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
