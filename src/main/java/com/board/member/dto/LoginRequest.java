package com.board.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    @Email(message = "이메일 형식이 올바르지 않아용")
    @NotBlank(message = "이메일은 필수 입력값이에용")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값이에용")
    private String password;
}
