package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
import com.exception.ErrorType;

public class InvalidToken extends CustomException {
    private InvalidToken(ErrorType exceptionType) {
        super(exceptionType);
    }

    public static InvalidToken getInstance() {
        return new InvalidToken(ErrorCodeType.INVALID_REFRESH_TOKEN);
    }
}
