package com.board.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleUpdateRequest {

    @NotBlank
    private final String title;
    @NotBlank
    private final String content;
}
