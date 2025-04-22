package com.member.controller;

import com.member.dto.request.LoginRequest;
import com.member.dto.request.MemberSignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.MemberSignUpResponse;
import com.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberSignUpResponse> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        MemberSignUpResponse memberSignUpResponse = memberService.signUp(request);
        return ResponseEntity.ok(memberSignUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {
        LoginResponse loginResponse = memberService.login(loginRequest);

        Cookie cookie = new Cookie("token", loginResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) loginResponse.getExpirationTime());
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }
}
