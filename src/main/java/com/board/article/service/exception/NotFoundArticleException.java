package com.board.article.service.exception;

import com.board.article.exception.exceptions.ArticleErrorCode;
import com.board.article.exception.exceptions.ArticleException;

public class NotFoundArticleException extends ArticleException {

    public NotFoundArticleException() {
        super(ArticleErrorCode.NOT_FOUND_ARTICLE);
    }
}
