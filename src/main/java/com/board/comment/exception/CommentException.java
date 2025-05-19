package com.board.comment.exception;

import com.board.common.exception.exceptions.BaseErrorCode;
import com.board.common.exception.exceptions.BaseException;

public class CommentException extends BaseException {

    public CommentException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
