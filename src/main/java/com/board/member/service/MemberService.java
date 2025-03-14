package com.board.member.service;

import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;

public interface MemberService {

    MemberSignUpResponse signUp(MemberSignUpRequest request);
}
