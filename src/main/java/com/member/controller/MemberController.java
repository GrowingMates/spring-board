package com.member.controller;

import com.config.auth.annotation.AuthenticatedMember;
import com.member.service.MemberService;
import com.util.cookie.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

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
