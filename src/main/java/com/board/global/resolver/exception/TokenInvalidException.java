package com.board.global.resolver.exception;

import com.board.global.exception.GlobalErrorCode;
import com.board.global.exception.GlobalException;

public class TokenInvalidException extends GlobalException {

    public TokenInvalidException() {
        super(GlobalErrorCode.INVALID_TOKEN);
    }
}
