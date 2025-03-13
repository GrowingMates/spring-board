package com.member.controller;

import com.member.dto.MemberSignUpRequest;
import com.member.dto.MemberSignUpResponse;
import com.member.service.MemberService;
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
    public ResponseEntity<MemberSignUpResponse> signUp(@RequestBody MemberSignUpRequest request) {
        MemberSignUpResponse memberSignUpResponse = memberService.signUp(request);
        return ResponseEntity.ok(memberSignUpResponse);
    }
}
