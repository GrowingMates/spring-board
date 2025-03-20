package com.board.exception.custom;

import com.board.exception.ErrorCode;
import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String email;

    public EmailNotFoundException(ErrorCode errorCode, String email) {
        this.errorCode = errorCode;
        this.email = email;
    }

    public static EmailNotFoundException from(String email) {
        return new EmailNotFoundException(
                ErrorCode.ENTITY_NOT_FOUND,
                email
        );
    }

}
