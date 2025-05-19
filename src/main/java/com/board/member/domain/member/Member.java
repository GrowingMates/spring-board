package com.board.member.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String memberName;

    @Column(nullable = false)
    @NotBlank
    private String memberNickName;

    @Column(nullable = false)
    @NotBlank
    private String memberLoginId;

    @Column(nullable = false)
    @NotBlank
    private String memberPassword;

    public Member(String memberName, String memberNickName, String memberLoginId, String memberPassword) {
        this.memberName = memberName;
        this.memberNickName = memberNickName;
        this.memberLoginId = memberLoginId;
        this.memberPassword = memberPassword;
    }

    public void update(String memberName, String memberNickName, String memberLoginId, String memberPassword) {
        if (memberName != null) {
            this.memberName = memberName;
        }
        if (memberNickName != null) {
            this.memberNickName = memberNickName;
        }
        if (memberLoginId != null) {
            this.memberLoginId = memberLoginId;
        }
        if (memberPassword != null) {
            this.memberPassword = memberPassword;
        }
    }
}
