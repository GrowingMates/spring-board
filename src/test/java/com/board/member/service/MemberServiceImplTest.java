package com.board.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.board.config.jwt.JwtUtil;
import com.board.exception.SingUpException;
import com.board.member.dto.LoginRequest;
import com.board.member.dto.LoginResponse;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.entity.MemberEntity;
import com.board.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    private MemberEntity member;

    @BeforeEach
    void setUp() {
        member = MemberEntity.builder()
                .email("test@example.com")
                .password("1234")
                .nickName("testUser")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest("test@example.com", "1234", "testUser");
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(member);

        // When
        MemberSignUpResponse response = memberService.signUp(request);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("testUser", response.getNickName());
    }

    @Test
    @DisplayName("회원가입 중복 이메일 에러")
    void signUp_DuplicateEmail_ThrowsException() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest("test@example.com", "1234", "testUser");
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(SingUpException.class, () -> memberService.signUp(request));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "1234");
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));
        when(jwtUtil.generateToken(any(String.class), any(Long.class))).thenReturn("token");

        // When
        LoginResponse response = memberService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> memberService.login(request));
    }

    @Test
    @DisplayName("이메일로 멤버 찾기 성공")
    void findByEmail_Success() {
        // Given
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

        // When
        MemberEntity foundMember = memberService.findByEmail("test@example.com");

        // Then
        assertNotNull(foundMember);
        assertEquals("test@example.com", foundMember.getEmail());
    }

    @Test
    @DisplayName("ID로 멤버 찾기 성공")
    void findById_Success() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When
        MemberEntity foundMember = memberService.findById(1L);

        // Then
        assertNotNull(foundMember);
        assertEquals("test@example.com", foundMember.getEmail());
    }
}
