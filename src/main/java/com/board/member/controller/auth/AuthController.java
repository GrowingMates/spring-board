package com.board.member.controller.auth;

import com.board.member.controller.auth.dto.request.LoginRequest;
import com.board.member.controller.auth.dto.request.SignUpRequest;
import com.board.member.controller.auth.dto.response.SignUpResponse;
import com.board.member.service.auth.AuthService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        Long memberId = authService.signUp(request.loginId(), request.memberName(), request.memberNickName(),
                request.password());
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(memberId)
                .toUri();

        return ResponseEntity.created(location).body(new SignUpResponse(memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<HttpHeaders> login(@RequestBody LoginRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + authService.login(request.loginId(), request.password()));
        return ResponseEntity.status(HttpStatus.OK).body(httpHeaders);
    }
}
