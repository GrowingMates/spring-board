package com.board.comment.exception.exceptions;

import lombok.Getter;

@Getter
public class CommentException extends RuntimeException{

    private final CommentErrorCode errorCode;

    public CommentException(CommentErrorCode errorCode) {
        super(errorCode.getCustomCode() + ": " + errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
