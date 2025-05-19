package com.board.common.exception.exceptions;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus httpStatus();
    String customCode();
    String message();
}
