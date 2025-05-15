package com.board.entity;

import com.common.entity.SoftDeletedEntity;
import com.exception.custom.DifferentOwnerException;
import com.exception.custom.NotIncludeBoardException;
import com.member.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentEntity extends SoftDeletedEntity {

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

    public CommentEntity(String content, ArticleEntity article, MemberEntity member) {
        this.content = content;
        this.article = article;
        this.member = member;
    }

    @Builder
    public CommentEntity(Long id, String content, ArticleEntity article, MemberEntity member) {
        this.id = id;
        this.content = content;
        this.article = article;
        this.member = member;
    }

    public void validateOwner(MemberEntity member) {
        if (!this.member.equals(member)) {
            throw DifferentOwnerException.from(this.member.getEmail());
        }
    }

    public void update(String content, MemberEntity modifier) {
        validateOwner(modifier);
        this.content = content;
    }

    public void softDelete(MemberEntity deleter) {
        validateOwner(deleter);
        super.softDelete();
    }

    public void validateArticle(Long articleId) {
        if (!this.article.getId().equals(articleId)) {
            throw NotIncludeBoardException.from(articleId);
        }
    }
}
