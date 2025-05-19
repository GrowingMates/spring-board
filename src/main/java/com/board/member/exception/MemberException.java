package com.board.member.exception;

import com.board.common.exception.exceptions.BaseErrorCode;
import com.board.common.exception.exceptions.BaseException;

public class MemberException extends BaseException {

    public MemberException(BaseErrorCode errorCode) {
       super(errorCode);
    }
}
