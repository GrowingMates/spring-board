package com.board.dto.response;

import com.board.entity.ArticleEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArticleResponse {

    private final Long id;
    private final String title;
    private String content;
    private final Long memberId;
    private final long viewCount;

    @Builder
    public ArticleResponse(Long id, String title, String content, Long memberId, long viewCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.viewCount = viewCount;
    }

    public ArticleResponse(ArticleEntity article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.memberId = article.getMember().getId();
        this.viewCount = article.getViewCount();
    }

    public static ArticleResponse withoutContent(ArticleEntity article) {
        return new ArticleResponse(article.getId(),
                article.getTitle(),
                article.getMember().getId(),
                article.getViewCount());
    }
}
