package com.member.service;

import com.member.dto.MemberSignUpRequest;
import com.member.dto.MemberSignUpResponse;

public interface MemberService {

    MemberSignUpResponse signUp(MemberSignUpRequest request);
}
