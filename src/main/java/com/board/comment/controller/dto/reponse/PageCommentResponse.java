package com.board.comment.controller.dto.reponse;

import com.board.comment.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Page;

public record PageCommentResponse(
        List<CommentResponse> commentResponses,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    public PageCommentResponse(Page<Comment> page) {
        this(
                page.map(comment -> new CommentResponse(
                        comment.getMemberId(),
                        comment.getArticleId(),
                        comment.getContent()
                )).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
