package com.board.board.dto;

import com.board.board.domain.Article;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArticleResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long memberId;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.memberId = article.getMember().getId();
    }
}
