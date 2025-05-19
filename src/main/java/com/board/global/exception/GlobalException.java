package com.board.global.exception;

import com.board.common.exception.exceptions.BaseErrorCode;
import com.board.common.exception.exceptions.BaseException;

public class GlobalException extends BaseException {

    public GlobalException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
