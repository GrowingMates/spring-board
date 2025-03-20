package com.board.member.exception.exceptionhandler.dto;

public record MemberErrorResponse(
        String customCode,
        String message
) {
}
