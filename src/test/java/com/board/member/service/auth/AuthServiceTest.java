package com.board.member.service.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.board.member.controller.auth.dto.request.LoginRequest;
import com.board.member.controller.auth.dto.request.SignUpRequest;
import com.board.member.domain.auth.TokenProvider;
import com.board.member.domain.member.Member;
import com.board.member.exception.MemberErrorCode;
import com.board.member.exception.MemberException;
import com.board.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Member member;
    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        member = new Member("홍길동", "길동이", "gildong123", "1234");
        ReflectionTestUtils.setField(member, "id", 1L);
        signUpRequest = new SignUpRequest("홍길동", "길동이", "gildong123", "1234");
        loginRequest = new LoginRequest("gildong123", "1234");
    }

    @Nested
    class 회원가입_테스트 {

        @Test
        @DisplayName("정상적으로 회원가입을 수행한다.")
        void signUpSuccess() {
            // given
            given(memberRepository.existsByMemberLoginId("gildong123")).willReturn(false);
            given(memberRepository.existsByMemberNickName("길동이")).willReturn(false);
            given(passwordEncoder.encode("1234")).willReturn("encoded-password");
            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", 1L);
                return saved;
            });

            // when
            Long memberId = authService.signUp(signUpRequest.loginId(), signUpRequest.memberName(), signUpRequest.memberNickName(),
                    signUpRequest.password());

            // then
            assertThat(memberId).isEqualTo(1L);
        }

        @Test
        @DisplayName("중복된 아이디로 회원가입할 경우 예외가 발생한다.")
        void signUpDuplicateLoginId() {
            // given
            given(memberRepository.existsByMemberLoginId("gildong123")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(signUpRequest.loginId(), signUpRequest.memberName(), signUpRequest.memberNickName(),
                    signUpRequest.password()))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(MemberErrorCode.DUPLICATE_LOGIN_ID.message());
        }

        @Test
        @DisplayName("중복된 닉네임으로 회원가입할 경우 예외가 발생한다.")
        void signUpDuplicateNickName() {
            // given
            given(memberRepository.existsByMemberLoginId("gildong123")).willReturn(false);
            given(memberRepository.existsByMemberNickName("길동이")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(signUpRequest.loginId(), signUpRequest.memberName(), signUpRequest.memberNickName(),
                    signUpRequest.password()))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(MemberErrorCode.DUPLICATE_NICKNAME.message());
        }
    }

    @Nested
    class 로그인_테스트 {

        @Test
        @DisplayName("정상적으로 로그인을 수행한다.")
        void loginSuccess() {
            // given
            Long memberId = 1L;
            given(memberRepository.findMemberByMemberLoginId("gildong123")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("1234", "1234")).willReturn(true);
            given(tokenProvider.create(memberId)).willReturn("fake-jwt-token");

            // when
            String token = authService.login(loginRequest.loginId(), loginRequest.password());

            // then
            assertThat(token).isEqualTo("fake-jwt-token");
        }

        @Test
        @DisplayName("존재하지 않는 아이디로 로그인 시도 시 예외가 발생한다.")
        void loginInvalidLoginId() {
            // given
            given(memberRepository.findMemberByMemberLoginId("gildong123")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest.loginId(), loginRequest.password()))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(MemberErrorCode.NOT_MATCH_LOGIN_ID.message());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않을 경우 예외가 발생한다.")
        void loginInvalidPassword() {
            // given
            Member wrongPasswordMember = new Member("홍길동", "길동이", "gildong123", "다른비밀번호");
            given(memberRepository.findMemberByMemberLoginId("gildong123")).willReturn(Optional.of(wrongPasswordMember));

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest.loginId(), loginRequest.password()))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(MemberErrorCode.NOT_MATCH_PASSWORD.message());
        }
    }
}
