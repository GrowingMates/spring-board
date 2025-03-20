package com.board.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSignUpRequest {

    @NotBlank(message = "이메일은 필수 입력값이에용")
    @Email
    private final String email;

    @NotBlank(message = "닉네임은 필수 입력값이에용")
    private final String nickName;

    @NotBlank(message = "비밀번호는 필수 입력값이에용")
    private final String password;

    @Builder
    public MemberSignUpRequest(String email, String nickName, String password) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
    }
}
