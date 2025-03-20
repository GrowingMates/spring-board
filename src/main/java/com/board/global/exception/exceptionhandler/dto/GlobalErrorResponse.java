package com.board.global.exception.exceptionhandler.dto;

public record GlobalErrorResponse(
        String customCode,
        String message
) {
}
