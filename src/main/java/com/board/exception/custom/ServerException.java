package com.board.exception.custom;

import com.board.exception.CustomException;
import com.board.exception.ErrorCodeType;
import lombok.Getter;

@Getter
public class ServerException extends CustomException {

    private ServerException() {
        super(ErrorCodeType.SYSTEM_ERROR);
    }

    public static ServerException getInstance() {
        return new ServerException();
    }
}
