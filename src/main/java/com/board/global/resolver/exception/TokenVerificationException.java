package com.board.global.resolver.exception;

import com.board.global.exception.exceptions.GlobalErrorCode;
import com.board.global.exception.exceptions.GlobalException;

public class TokenVerificationException extends GlobalException {

    public TokenVerificationException() {
        super(GlobalErrorCode.CAN_NOT_VERIFY_TOKEN);
    }
}
