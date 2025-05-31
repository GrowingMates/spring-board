package com.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    private Long parentId;

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    @Builder
    public CommentCreateRequest(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }
}
