package com.member.dto;

import com.member.entity.MemberEntity;
import lombok.Getter;

@Getter
public class MemberSignUpResponse {

    private final String email;
    private final String nickName;

    public MemberSignUpResponse(MemberEntity memberEntity) {
        this.email = memberEntity.getEmail();
        this.nickName = memberEntity.getNickName();
    }
}
