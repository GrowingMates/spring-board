package com.board.member.controller.member;

import com.board.global.resolver.annotation.Auth;
import com.board.member.controller.member.dto.reponse.MemberResponse;
import com.board.member.controller.member.dto.request.MemberRequest;
import com.board.member.service.member.MemberService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(title = "My API", version = "1.0", description = "API Documentation")
)
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    public ResponseEntity<MemberResponse> showMember(@Auth Long memberId) {
        return ResponseEntity.ok(memberService.showMember(memberId));
    }

    @PatchMapping("/members")
    public ResponseEntity<MemberResponse> updateMember(@Auth Long memberId, @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(memberId, request));
    }

    @DeleteMapping("/members")
    public ResponseEntity<MemberResponse> deleteMember(@Auth Long memberId) {
        return ResponseEntity.ok(memberService.deleteMember(memberId));
    }
}
