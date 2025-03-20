package com.board.global.resolver.exception;

import com.board.global.exception.exceptions.GlobalErrorCode;
import com.board.global.exception.exceptions.GlobalException;

public class TokenInvalidException extends GlobalException {

    public TokenInvalidException() {
        super(GlobalErrorCode.INVALID_TOKEN);
    }
}
