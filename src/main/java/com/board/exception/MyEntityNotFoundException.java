package com.board.exception;

import lombok.Getter;

@Getter
public class MyEntityNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final long entityId;

    private MyEntityNotFoundException(ErrorCode errorCode, long entityId) {
        this.errorCode = errorCode;
        this.entityId = entityId;
    }

    public static MyEntityNotFoundException of(long entityId) {
        return new MyEntityNotFoundException(
                ErrorCode.ENTITY_NOT_FOUND,
                entityId
        );
    }
}
