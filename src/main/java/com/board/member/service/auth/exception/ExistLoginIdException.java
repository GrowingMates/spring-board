package com.board.member.service.auth.exception;

import com.board.member.exception.exceptions.MemberErrorCode;
import com.board.member.exception.exceptions.MemberException;

public class ExistLoginIdException extends MemberException {

    public ExistLoginIdException() {
        super(MemberErrorCode.DUPLICATE_LOGIN_ID);
    }
}
