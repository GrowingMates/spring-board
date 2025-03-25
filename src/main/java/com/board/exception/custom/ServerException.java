package com.board.exception.custom;

import com.board.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServerException extends RuntimeException {
    private final ErrorCode errorCode;

    public static ServerException getInstance() {
        return new ServerException(ErrorCode.SYSTEM_ERROR);
    }
}
