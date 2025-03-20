package com.board.global.resolver.exception;

import com.board.global.exception.exceptions.GlobalErrorCode;
import com.board.global.exception.exceptions.GlobalException;

public class TokenExpirationException extends GlobalException {

    public TokenExpirationException() {
        super(GlobalErrorCode.EXPIRED_TOKEN);
    }
}
