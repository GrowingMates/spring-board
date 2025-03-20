package com.board.comment.exception.exceptionhandler.dto;

public record CommentErrorResponse(
        String customCode,
        String message
) {
}
