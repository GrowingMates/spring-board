package com.board.article.controller.dto.response;

public record ArticleResponse(
        Long articleId,
        Long memberId,
        String title,
        String content
) {
}
