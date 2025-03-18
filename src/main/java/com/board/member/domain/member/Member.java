package com.board.member.domain.member;

import com.board.member.domain.member.exception.NotMatchPasswordException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
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

    public void checkPassword(String password) {
        if(!Objects.equals(memberPassword, password)) {
            throw new NotMatchPasswordException();
        }
    }
}
