package com.board.article.controller.dto.response;

import com.board.article.domain.Article;
import java.util.List;
import org.springframework.data.domain.Page;

public record PageArticleResponse(
        List<ArticleResponse> articleResponses,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    public PageArticleResponse(Page<Article> page) {
        this(
                page.map(article -> new ArticleResponse(
                        article.getId(),
                        article.getMemberId(),
                        article.getTitle(),
                        article.getContent()
                )).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
