package com.member.service;

import com.config.jwt.JwtUtil;
import com.config.jwt.TokenWithExpiration;
import com.config.jwt.token.RefreshToken;
import com.config.jwt.token.RefreshTokenService;
import com.exception.custom.InvalidToken;
import com.exception.custom.LoginException;
import com.exception.custom.MyEntityNotFoundException;
import com.exception.custom.SignUpException;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.SignUpResponse;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private RefreshTokenService refreshTokenService;

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

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTests {

        @Test
        @DisplayName("회원가입 성공")
        void signUp_Success() {
            // Given
            SignUpRequest request = new SignUpRequest("test@example.com", "1234", "testUser");
            when(memberRepository.save(any(MemberEntity.class))).thenReturn(member);

            // When
            SignUpResponse response = memberService.signUp(request);

            // Then
            assertNotNull(response);
            assertEquals("test@example.com", response.getEmail());
            assertEquals("testUser", response.getNickName());
        }

        @Test
        @DisplayName("회원가입 중복 이메일 에러")
        void signUp_DuplicateEmail_ThrowsException() {
            // Given
            SignUpRequest request = new SignUpRequest("test@example.com", "1234", "testUser");
            when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));

            // When & Then
            assertThrows(SignUpException.class, () -> memberService.signUp(request));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTests {
        @Test
        @DisplayName("로그인 성공")
        void login_Success() {
            // Given
            LoginRequest request = new LoginRequest("test@example.com", "1234");
            when(memberRepository.findByEmailAndIsDeletedFalse(request.getEmail())).thenReturn(Optional.of(member));

            TokenWithExpiration accessToken = new TokenWithExpiration("token", 3600000L);
            TokenWithExpiration refreshToken = new TokenWithExpiration("refreshToken", 604800000L);

            when(jwtUtil.generateAccessToken(any())).thenReturn(accessToken);
            when(jwtUtil.generateRefreshToken(any())).thenReturn(refreshToken);

            // When
            LoginResponse response = memberService.login(request);

            // Then
            assertNotNull(response);
            assertEquals("token", response.getAccessToken().getToken());
            assertEquals(3600000L, response.getAccessToken().getExpiration());
            assertEquals("refreshToken", response.getRefreshToken().getToken());
            assertEquals(604800000L, response.getRefreshToken().getExpiration());
        }

        @Test
        @DisplayName("로그인 실패 - 잘못된 비밀번호")
        void login_Fail_WrongPassword() {
            // Given
            LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
            when(memberRepository.findByEmailAndIsDeletedFalse(request.getEmail())).thenReturn(Optional.of(member));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> memberService.login(request));
        }

        @Test
        @DisplayName("로그인 실패 - 이메일이 존재하지 않음")
        void login_Fail_EmailNotFound() {
            // Given
            LoginRequest request = new LoginRequest("nonexistent@example.com", "1234");
            when(memberRepository.findByEmailAndIsDeletedFalse(request.getEmail())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(LoginException.class, () -> memberService.login(request));
        }
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

    @Test
    @DisplayName("유효한 리프래시 토큰으로 새 액세스 토큰 발급")
    void reissueAccessToken_유효한리프래시토큰() {
        // Given
        Long memberId = 1L;
        String refreshTokenValue = "validRefreshToken";
        RefreshToken refreshToken = new RefreshToken(memberId, refreshTokenValue);
        TokenWithExpiration newAccessToken = new TokenWithExpiration("newAccessToken", 3600L);

        when(refreshTokenService.findByMemberId(memberId)).thenReturn(refreshToken);
        when(jwtUtil.isTokenValid(refreshTokenValue)).thenReturn(true);
        when(jwtUtil.generateAccessToken(memberId)).thenReturn(newAccessToken);

        // When
        LoginResponse response = memberService.reissueAccessToken(memberId);

        // Then
        assertThat(response.getAccessToken().getToken()).isEqualTo("newAccessToken");
    }

    @Test
    @DisplayName("유효하지 않은 리프래시 토큰으로 예외 발생")
    void reissueAccessToken_유효하지않은리프래시토큰() {
        // Given
        Long memberId = 1L;
        String refreshTokenValue = "invalidRefreshToken";
        RefreshToken refreshToken = new RefreshToken(memberId, refreshTokenValue);

        when(refreshTokenService.findByMemberId(memberId)).thenReturn(refreshToken);
        when(jwtUtil.isTokenValid(refreshTokenValue)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> memberService.reissueAccessToken(memberId))
                .isInstanceOf(InvalidToken.class);
    }

    @Test
    @DisplayName("reissueAccessToken - 멤버 ID로 리프래시 토큰을 찾을 수 없을 때 MyEntityNotFoundException 발생")
    void reissueAccessToken_리프래시토큰없음() {
        // Given
        Long memberId = 1L;
        when(refreshTokenService.findByMemberId(memberId)).thenThrow(MyEntityNotFoundException.from(memberId));

        // When & Then
        assertThatThrownBy(() -> memberService.reissueAccessToken(memberId))
                .isInstanceOf(MyEntityNotFoundException.class);
    }


}
