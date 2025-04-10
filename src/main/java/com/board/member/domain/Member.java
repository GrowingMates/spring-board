package com.board.member.domain;

import com.board.member.entity.MemberEntity;
import com.board.member.message.ErrorMessage;
import lombok.Getter;

@Getter
public class Member {

    private final String email;
    private final String password;

    public Member(MemberEntity memberEntity) {
        this.email = memberEntity.getEmail();
        this.password = memberEntity.getPassword();
    }

    public void checkPassword(String password) {
        if (!isEqualPassword(password)) {
            throw new IllegalArgumentException(ErrorMessage.NOT_CORRECT_LOGIN);
        }
    }

    private boolean isEqualPassword(String password) {
        return this.password.equals(password);
    }
}
