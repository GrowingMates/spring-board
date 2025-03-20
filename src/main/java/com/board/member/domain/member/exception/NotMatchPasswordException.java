package com.board.member.domain.member.exception;

import com.board.member.exception.exceptions.MemberErrorCode;
import com.board.member.exception.exceptions.MemberException;

public class NotMatchPasswordException extends MemberException {

    public NotMatchPasswordException() {
        super(MemberErrorCode.NOT_MATCH_PASSWORD);
    }
}
