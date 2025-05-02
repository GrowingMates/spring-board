package com.member.service;

import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.SignUpResponse;
import com.member.entity.MemberEntity;

public interface MemberService {

    SignUpResponse signUp(SignUpRequest request);

    LoginResponse login(LoginRequest request);

    MemberEntity findByEmail(String email);

    MemberEntity findById(Long id);

    void logout(Long memberId);

    void withdraw(Long memberId);

}
