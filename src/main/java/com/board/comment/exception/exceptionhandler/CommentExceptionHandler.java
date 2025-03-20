package com.board.comment.exception.exceptionhandler;

import com.board.comment.exception.exceptionhandler.dto.CommentErrorResponse;
import com.board.comment.exception.exceptions.CommentErrorCode;
import com.board.comment.exception.exceptions.CommentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<CommentErrorResponse> handleException(CommentException e) {
        CommentErrorCode commentErrorCode = e.getErrorCode();
        CommentErrorResponse response = new CommentErrorResponse(
                commentErrorCode.getCustomCode(),
                commentErrorCode.getMessage()
        );

        return ResponseEntity.status(commentErrorCode.getHttpStatus()).body(response);
    }
}
