package com.board.exception;

import org.springframework.http.HttpStatus;

public interface ErrorType {
    String getCode();

    String getMessage();

    HttpStatus getHttpStatus();

}
