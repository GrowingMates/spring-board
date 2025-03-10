package com.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MyEntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(MyEntityNotFoundException e) {

        log.error("Error Code: {}, Message: {}, entityId: {}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getEntityId(),
                e);

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        errorResponse.addDetail("entityId : ", e.getEntityId());

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

}
