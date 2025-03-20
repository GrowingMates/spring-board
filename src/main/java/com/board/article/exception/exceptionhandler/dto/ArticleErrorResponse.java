package com.board.article.exception.exceptionhandler.dto;

public record ArticleErrorResponse(
        String customCode,
        String message
) {
}
