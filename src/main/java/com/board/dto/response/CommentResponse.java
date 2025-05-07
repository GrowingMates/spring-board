package com.board.dto.response;

import com.board.entity.CommentEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final Long authorId;
    private final String authorName;
    private final LocalDateTime createdAt;
    private final boolean isDeleted;

    public CommentResponse(CommentEntity comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorId = comment.getMember().getId();
        this.authorName = comment.getMember().getNickName();
        this.createdAt = comment.getCreatedAt();
        this.isDeleted = comment.isDeleted();
    }
}
