package com.member.dto.response;

import com.member.entity.MemberEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberSignUpResponse {

    private final Long id;
    private final String email;
    private final String nickName;

    public MemberSignUpResponse(MemberEntity memberEntity) {
        this.id = memberEntity.getId();
        this.email = memberEntity.getEmail();
        this.nickName = memberEntity.getNickName();
    }
}
