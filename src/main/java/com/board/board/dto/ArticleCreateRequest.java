package com.board.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ArticleCreateRequest {

    private String title;
    private String content;
    private Long memberId;

    public ArticleCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Builder
    public ArticleCreateRequest(String title, String content, Long memberId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
    }
}
