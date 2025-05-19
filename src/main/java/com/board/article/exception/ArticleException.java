package com.board.article.exception;

import com.board.common.exception.exceptions.BaseErrorCode;
import com.board.common.exception.exceptions.BaseException;

public class ArticleException extends BaseException {

    public ArticleException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
