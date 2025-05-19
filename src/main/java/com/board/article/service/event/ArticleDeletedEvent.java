package com.board.article.service.event;

public record ArticleDeletedEvent(
        Long articleId
) {
}
