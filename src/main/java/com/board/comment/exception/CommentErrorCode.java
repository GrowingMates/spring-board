package com.board.comment.exception;

import com.board.common.exception.exceptions.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum CommentErrorCode implements BaseErrorCode {

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "C001", "댓글을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS_COMMENT(HttpStatus.BAD_REQUEST, "C002", "댓글에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;

    CommentErrorCode(HttpStatus httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String customCode() {
        return customCode;
    }

    @Override
    public String message() {
        return message;
    }
}
