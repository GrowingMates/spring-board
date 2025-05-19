package com.board.global.resolver.exception;

import com.board.global.exception.GlobalErrorCode;
import com.board.global.exception.GlobalException;

public class TokenExpirationException extends GlobalException {

    public TokenExpirationException() {
        super(GlobalErrorCode.EXPIRED_TOKEN);
    }
}
