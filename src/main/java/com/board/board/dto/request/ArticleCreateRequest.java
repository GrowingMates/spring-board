package com.board.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ArticleCreateRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @Builder
    public ArticleCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
