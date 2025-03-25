package com.board.member.service;

import com.board.member.dto.request.LoginRequest;
import com.board.member.dto.request.MemberSignUpRequest;
import com.board.member.dto.response.LoginResponse;
import com.board.member.dto.response.MemberSignUpResponse;
import com.board.member.entity.MemberEntity;

public interface MemberService {

    MemberSignUpResponse signUp(MemberSignUpRequest request);

    LoginResponse login(LoginRequest request);

    MemberEntity findByEmail(String email);

    MemberEntity findById(Long id);

}
