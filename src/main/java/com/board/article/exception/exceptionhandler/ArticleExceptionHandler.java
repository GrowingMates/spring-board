package com.board.article.exception.exceptionhandler;

import com.board.article.exception.exceptionhandler.dto.ArticleErrorResponse;
import com.board.article.exception.exceptions.ArticleErrorCode;
import com.board.article.exception.exceptions.ArticleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ArticleExceptionHandler {

    @ExceptionHandler(ArticleException.class)
    public ResponseEntity<ArticleErrorResponse> handleException(ArticleException e) {
        ArticleErrorCode articleErrorCode = e.getErrorCode();
        ArticleErrorResponse response = new ArticleErrorResponse(
                articleErrorCode.getCustomCode(),
                articleErrorCode.getMessage()
        );

        return ResponseEntity.status(articleErrorCode.getHttpStatus()).body(response);
    }
}
