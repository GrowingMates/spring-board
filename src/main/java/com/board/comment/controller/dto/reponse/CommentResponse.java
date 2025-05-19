package com.board.comment.controller.dto.reponse;

public record CommentResponse(
        Long memberId,
        Long articleId,
        String content
) {
}
