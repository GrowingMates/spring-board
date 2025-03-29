package com.board.board.entity;

import com.board.exception.custom.DifferentOwnerException;
import com.board.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleEntity {

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

    @Builder
    public ArticleEntity(String title, String content, MemberEntity member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public ArticleEntity(Long id, String title, String content, MemberEntity member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
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
