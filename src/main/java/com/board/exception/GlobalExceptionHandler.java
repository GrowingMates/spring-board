package com.board.exception;

import static com.board.exception.ErrorCodeType.VALIDATION_ERROR;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        log.error("Error Code: {}, Message: {}, Additional Details: {}",
                e.getExceptionType().getCode(),
                e.getExceptionType().getMessage(),
                e.getAdditionalDetails(),
                e);

        ErrorResponse errorResponse = ErrorResponse.of(e.getExceptionType());
        e.getAdditionalDetails().forEach((key, value) -> errorResponse.addDetail(key, value));

        return new ResponseEntity<>(errorResponse, e.getExceptionType().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        ErrorResponse errorResponse = ErrorResponse.of(VALIDATION_ERROR);

        for (FieldError fieldError : fieldErrors) {
            errorResponse.addDetail(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errorResponse, VALIDATION_ERROR.getHttpStatus());
    }

}
