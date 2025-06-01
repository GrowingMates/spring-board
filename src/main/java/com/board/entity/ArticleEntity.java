package com.board.entity;

import com.common.entity.SoftDeletedEntity;
import com.exception.custom.DifferentOwnerException;
import com.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "article")
public class ArticleEntity extends SoftDeletedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(nullable = false)
    private long viewCount = 0;

    public ArticleEntity(String title, String content, MemberEntity member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    @Builder
    public ArticleEntity(Long id, String title, String content, MemberEntity member, long viewCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.viewCount = viewCount;
    }

    public void increaseViewCount(long viewCount) {
        this.viewCount += viewCount;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validateOwner(MemberEntity member) {
        if (!this.member.equals(member)) {
            throw DifferentOwnerException.from(this.member.getEmail());
        }
    }
}
