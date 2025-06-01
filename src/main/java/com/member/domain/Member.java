package com.member.domain;

import com.member.entity.MemberEntity;
import com.member.message.ErrorMessage;
import lombok.Getter;

@Getter
public class Member {

    private final Long id;
    private final String email;
    private final String password;

    public Member(MemberEntity memberEntity) {
        this.id = memberEntity.getId();
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
