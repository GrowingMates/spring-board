package com.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSignUpRequest {

    private final String email;
    private final String nickName;
    private final String password;

    @Builder
    public MemberSignUpRequest(String email, String nickName, String password) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
    }
}
