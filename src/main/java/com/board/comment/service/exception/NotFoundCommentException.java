package com.board.comment.service.exception;

import com.board.comment.exception.exceptions.CommentErrorCode;
import com.board.comment.exception.exceptions.CommentException;

public class NotFoundCommentException extends CommentException {

    public NotFoundCommentException() {
        super(CommentErrorCode.NOT_FOUND_COMMENT);
    }
}
