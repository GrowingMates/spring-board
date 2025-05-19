package com.board.comment.domain;

import com.board.comment.exception.CommentErrorCode;
import com.board.comment.exception.CommentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column
    private Long articleId;


    @Column(nullable = false)
    private String content;

    public Comment(Long memberId, Long articleId, String content) {
        this.memberId = memberId;
        this.articleId = articleId;
        this.content = content;
    }

    public void update(String content) {
        if (content != null) {
            this.content = content;
        }
    }

    public void validateAccessAboutComment(Long memberId) {
        if(!Objects.equals(this.memberId, memberId)) {
            throw new CommentException(CommentErrorCode.FORBIDDEN_ACCESS_COMMENT);
        }
    }
}
