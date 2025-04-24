package com.board.dto.response;

import com.board.entity.CommentEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final Long authorId;
    private final String authorName;
    private final LocalDateTime createdAt;
    private final boolean deleted;

    public CommentResponse(CommentEntity comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorId = comment.getMember().getId();
        this.authorName = comment.getMember().getNickName();
        this.createdAt = comment.getCreatedAt();
        this.deleted = comment.isDeleted();
    }
}
