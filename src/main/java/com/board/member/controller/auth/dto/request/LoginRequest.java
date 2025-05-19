package com.board.member.controller.auth.dto.request;

public record LoginRequest(
        String loginId,
        String password
) {
}
