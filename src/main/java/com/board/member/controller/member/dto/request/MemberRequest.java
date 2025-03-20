package com.board.member.controller.member.dto.request;

public record MemberRequest(
        String name,
        String nickName,
        String id,
        String password
) {
}
