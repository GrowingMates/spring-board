package com.board.entity;

import com.exception.custom.DifferentOwnerException;
import com.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private boolean isDeleted = false;

    public CommentEntity(String content, ArticleEntity article, MemberEntity member) {
        this.content = content;
        this.article = article;
        this.member = member;
    }

    public void validateOwner(MemberEntity member) {
        if (!this.member.equals(member)) {
            throw DifferentOwnerException.from(this.member.getEmail());
        }
    }

    public void update(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    @PrePersist
    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }
}
