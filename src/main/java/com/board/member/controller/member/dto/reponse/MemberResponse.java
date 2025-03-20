package com.board.member.controller.member.dto.reponse;

public record MemberResponse(
        String mame,
        String nickName,
        String id,
        String password
) {
}
