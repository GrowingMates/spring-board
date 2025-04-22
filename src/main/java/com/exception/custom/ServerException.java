package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
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
