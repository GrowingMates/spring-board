package com.member.controller;

import com.config.auth.annotation.AuthenticatedMember;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.SignUpResponse;
import com.member.service.MemberService;
import com.util.cookie.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse signUpResponse = memberService.signUp(request);
        return ResponseEntity.ok(signUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {
        LoginResponse loginResponse = memberService.login(loginRequest);

        Cookie cookie = CookieUtils.createCookie("token",
                loginResponse.getAccessToken(),
                (int) loginResponse.getExpirationTime());
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticatedMember Long memberId,
                                       HttpServletResponse response) {
        memberService.logout(memberId);

        Cookie cookie = CookieUtils.invalidateCookie("token");
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticatedMember Long memberId,
                                         HttpServletResponse response) {
        memberService.withdraw(memberId);

        Cookie cookie = CookieUtils.invalidateCookie("token");
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }
}
