package com.board.article.exception.exceptions;

import lombok.Getter;

@Getter
public class ArticleException extends RuntimeException{

    private final ArticleErrorCode errorCode;

    public ArticleException(ArticleErrorCode errorCode) {
        super(errorCode.getCustomCode() + ": " + errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
