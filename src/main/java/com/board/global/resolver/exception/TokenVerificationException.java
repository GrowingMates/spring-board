package com.board.global.resolver.exception;

import com.board.global.exception.GlobalErrorCode;
import com.board.global.exception.GlobalException;

public class TokenVerificationException extends GlobalException {

    public TokenVerificationException() {
        super(GlobalErrorCode.CAN_NOT_VERIFY_TOKEN);
    }
}
