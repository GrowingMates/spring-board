package com.board.article.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArticleErrorCode {

    NOT_FOUND_ARTICLE(HttpStatus.NOT_FOUND, "A001", "게시글을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS_ARTICLE(HttpStatus.BAD_REQUEST, "A002", "게시글에 대한 권한을 가지고 있지 않습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;

    ArticleErrorCode(HttpStatus httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }
}
