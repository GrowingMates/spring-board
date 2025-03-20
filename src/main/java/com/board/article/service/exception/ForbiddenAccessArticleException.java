package com.board.article.service.exception;

import com.board.article.exception.exceptions.ArticleErrorCode;
import com.board.article.exception.exceptions.ArticleException;

public class ForbiddenAccessArticleException extends ArticleException {

    public ForbiddenAccessArticleException() {
        super(ArticleErrorCode.FORBIDDEN_ACCESS_ARTICLE);
    }
}
