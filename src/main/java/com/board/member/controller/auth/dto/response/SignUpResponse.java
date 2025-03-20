package com.board.member.controller.auth.dto.response;

public record SignUpResponse(
        Long memberId,
        String token
) {
}
