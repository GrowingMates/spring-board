package com.board.member.service.auth.exception;

import com.board.member.exception.exceptions.MemberErrorCode;
import com.board.member.exception.exceptions.MemberException;

public class ExistNickNameException extends MemberException {

    public ExistNickNameException() {
        super(MemberErrorCode.DUPLICATE_NICKNAME);
    }
}
