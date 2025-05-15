package com.member.controller;

import com.config.auth.annotation.AuthenticatedMember;
import com.member.service.MemberService;
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
    public ResponseEntity<Void> logout(@AuthenticatedMember Long memberId) {
        memberService.logout(memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticatedMember Long memberId) {
        memberService.withdraw(memberId);
        return ResponseEntity.noContent().build();
    }
}
