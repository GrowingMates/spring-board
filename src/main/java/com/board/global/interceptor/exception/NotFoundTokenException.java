package com.board.global.interceptor.exception;

import com.board.global.exception.exceptions.GlobalErrorCode;
import com.board.global.exception.exceptions.GlobalException;

public class NotFoundTokenException extends GlobalException {

    public NotFoundTokenException() {
        super(GlobalErrorCode.NOT_FOUND_TOKEN);
    }
}
