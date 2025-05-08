package com.board.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "게시글 ID는 필수입니다")
    @Min(value = 1, message = "코멘트 ID는 1 이상이어야 합니다")
    private Long commentId;

    @Builder
    public CommentUpdateRequest(String content, Long commentId) {
        this.content = content;
        this.commentId = commentId;
    }
}
