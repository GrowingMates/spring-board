package com.board.member.controller.member.dto.reponse;

public record MemberResponse(
        String name,
        String nickName,
        String id,
        String password
) {
}
