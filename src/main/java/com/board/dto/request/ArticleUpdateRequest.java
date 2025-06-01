package com.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleUpdateRequest {

    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private final String title;
    @NotBlank(message = "내용이 입력되지 않았습니다.")
    private final String content;
}
