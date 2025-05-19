package com.board.member.domain.member.exception;

import com.board.member.exception.MemberErrorCode;
import com.board.member.exception.MemberException;

public class NotMatchPasswordException extends MemberException {

    public NotMatchPasswordException() {
        super(MemberErrorCode.NOT_MATCH_PASSWORD);
    }
}
