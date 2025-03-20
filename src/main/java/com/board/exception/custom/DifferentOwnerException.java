package com.board.exception.custom;

import com.board.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DifferentOwnerException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String email;

    public DifferentOwnerException(ErrorCode errorCode, String email) {
        this.errorCode = errorCode;
        this.email = email;
    }

    public static DifferentOwnerException from(String ownerEmail) {
        return new DifferentOwnerException(
                ErrorCode.AUTHORIZATION_ERROR,
                ownerEmail
        );
    }
}
