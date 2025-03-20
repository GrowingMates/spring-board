package com.board.comment.service.exception;

import com.board.comment.exception.exceptions.CommentErrorCode;
import com.board.comment.exception.exceptions.CommentException;

public class ForbiddenAccessCommentException extends CommentException {

    public ForbiddenAccessCommentException() {
        super(CommentErrorCode.FORBIDDEN_ACCESS_COMMENT);
    }
}
