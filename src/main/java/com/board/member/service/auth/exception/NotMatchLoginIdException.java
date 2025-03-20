package com.board.member.service.auth.exception;

import com.board.member.exception.exceptions.MemberErrorCode;
import com.board.member.exception.exceptions.MemberException;

public class NotMatchLoginIdException extends MemberException {

    public NotMatchLoginIdException() {
        super(MemberErrorCode.NOT_MATCH_LOGIN_ID);
    }
}
