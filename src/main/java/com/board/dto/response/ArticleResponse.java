package com.board.dto.response;

import com.board.entity.ArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class ArticleResponse {

    private final Long id;
    private final String title;
    private String content;
    private final Long memberId;
    private final long viewCount;

    public ArticleResponse(ArticleEntity article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.memberId = article.getMember().getId();
        this.viewCount = article.getViewCount();
    }

    public static ArticleResponse withoutContent(ArticleEntity article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                null, // content를 포함하지 않음
                article.getMember().getId(),
                article.getViewCount()
        );
    }
}
