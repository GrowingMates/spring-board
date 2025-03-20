package com.board.global.exception.exceptionhandler;

import com.board.global.exception.exceptionhandler.dto.GlobalErrorResponse;
import com.board.global.exception.exceptions.GlobalErrorCode;
import com.board.global.exception.exceptions.GlobalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleException(GlobalException e) {
        GlobalErrorCode errorCode = e.getErrorCode();
        GlobalErrorResponse response = new GlobalErrorResponse(errorCode.getCustomCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
