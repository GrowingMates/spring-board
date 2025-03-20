package com.board.member.service.member.exception;

import com.board.member.exception.exceptions.MemberErrorCode;
import com.board.member.exception.exceptions.MemberException;

public class NotFoundMemberException extends MemberException {

    public NotFoundMemberException() {
        super(MemberErrorCode.NOT_FOUND_MEMBER);
    }
}
