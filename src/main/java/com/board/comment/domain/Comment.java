package com.board.comment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
