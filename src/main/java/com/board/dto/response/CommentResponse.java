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
    private final Integer replyCount;

    public CommentResponse(CommentEntity comment, Integer replyCount) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorId = comment.getMember().getId();
        this.authorName = comment.getMember().getNickName();
        this.createdAt = comment.getCreatedAt();
        this.isDeleted = comment.isDeleted();
        this.replyCount = replyCount;
    }

    public static CommentResponse fromReply(CommentEntity comment) {
        return new CommentResponse(comment, null);
    }

    public static CommentResponse fromComment(CommentEntity comment, int replyCount) {
        return new CommentResponse(comment, replyCount);
    }

    public static CommentResponse of(CommentEntity comment, int replyCount) {
        if (comment.isReply()) {
            return fromReply(comment);
        }
        return fromComment(comment, replyCount);
    }
}
