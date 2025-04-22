package com.member.service;

import com.member.dto.request.LoginRequest;
import com.member.dto.request.MemberSignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.MemberSignUpResponse;
import com.member.entity.MemberEntity;

public interface MemberService {

    MemberSignUpResponse signUp(MemberSignUpRequest request);

    LoginResponse login(LoginRequest request);

    MemberEntity findByEmail(String email);

    MemberEntity findById(Long id);

}
