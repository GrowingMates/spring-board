package com.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    private Long commentId;

    @Builder
    public CommentUpdateRequest(String content, Long commentId) {
        this.content = content;
        this.commentId = commentId;
    }
}
