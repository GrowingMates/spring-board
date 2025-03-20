package com.board.exception;

import com.board.exception.custom.DifferentOwnerException;
import com.board.exception.custom.EmailNotFoundException;
import com.board.exception.custom.MyEntityNotFoundException;
import com.board.exception.custom.SignUpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.board.exception.ErrorCode.VALIDATION_ERROR;

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

    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ErrorResponse> SignUpException(SignUpException e) {

        log.error("Error Code: {}, Message: {}, errorMessage: {}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorMessage(),
                e);

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        errorResponse.addDetail("errorMessage : ", e.getErrorMessage());

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> signUpValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        ErrorResponse errorResponse = ErrorResponse.of(VALIDATION_ERROR);

        for (FieldError fieldError : fieldErrors) {
            errorResponse.addDetail(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errorResponse, VALIDATION_ERROR.getHttpStatus());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> emailNotFoundException(EmailNotFoundException e) {

        log.error("Error Code: {}, Message: {}, Email: {}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getEmail(),
                e);

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        errorResponse.addDetail("Email : ", e.getEmail());

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(DifferentOwnerException.class)
    public ResponseEntity<ErrorResponse> differentOwnerException(DifferentOwnerException e) {

        log.error("Error Code: {}, Message: {}, 기존 작성자 Email: {}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getEmail(),
                e);

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        errorResponse.addDetail("기존 작성자 Email : ", e.getEmail());

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

}
