package com.board.common.exception.exceptionHandler;

import com.board.common.exception.exceptionHandler.dto.ErrorResponse;
import com.board.common.exception.exceptions.BaseErrorCode;
import com.board.common.exception.exceptions.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        BaseErrorCode baseErrorCode = e.baseErrorCode();
        ErrorResponse response = new ErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus()).body(response);
    }
}
