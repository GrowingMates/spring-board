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
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(null) // content 포함 안함
                .memberId(article.getMember().getId())
                .viewCount(article.getViewCount())
                .build();
    }
}
