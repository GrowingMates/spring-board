package com.board.member.service;

import com.board.member.dto.LoginRequest;
import com.board.member.dto.LoginResponse;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.entity.MemberEntity;

public interface MemberService {

    MemberSignUpResponse signUp(MemberSignUpRequest request);

    LoginResponse login(LoginRequest request);

    MemberEntity findByEmail(String email);

}
