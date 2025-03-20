package com.board.member.controller.auth.dto.request;

public record SignUpRequest(
        String memberName,
        String memberNickName,
        String loginId,
        String password
) {
}
