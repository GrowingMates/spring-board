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

    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String title;
    @NotBlank(message = "내용이 입력되지 않았습니다.")
    private String content;

    @Builder
    public ArticleCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
