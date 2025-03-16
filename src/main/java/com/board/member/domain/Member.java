package com.board.member.domain;

import com.board.member.entity.MemberEntity;
import lombok.Getter;

@Getter
public class Member {

    private final String email;
    private final String password;

    public Member(MemberEntity memberEntity) {
        this.email = memberEntity.getEmail();
        this.password = memberEntity.getPassword();
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
