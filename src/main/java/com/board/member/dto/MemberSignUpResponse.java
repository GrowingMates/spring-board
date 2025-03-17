package com.board.member.dto;

import com.board.member.entity.MemberEntity;
import lombok.Getter;

@Getter
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
