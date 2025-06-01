package com.member.controller;

import com.config.auth.AuthUtil;
import com.config.auth.AuthenticatedMemberArgumentResolver;
import com.config.jwt.TokenWithExpiration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.dto.response.LoginResponse;
import com.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUtil authUtil;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        when(authenticatedMemberArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authenticatedMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(1L); // Long memberId 주입
    }

    @Test
    @DisplayName("로그아웃 테스트")
    public void logout_ShouldCallMemberServiceLogout() throws Exception {
        // Given
        Long memberId = 1L;

        // When: 로그아웃 요청
        mockMvc.perform(post("/members/logout"))
                .andExpect(status().isNoContent());

        // Then: MemberService의 logout 호출 확인
        verify(memberService).logout(memberId);
    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    public void withdraw_ShouldCallMemberServiceWithdraw() throws Exception {
        // Given
        Long memberId = 1L;

        // When: 회원탈퇴 요청
        mockMvc.perform(delete("/members/withdraw"))
                .andExpect(status().isNoContent());

        // Then: MemberService의 withdraw 호출 확인
        verify(memberService).withdraw(memberId);
    }

    @Test
    @DisplayName("리프레시 토큰 재발급 잘 되는지 테스트")
    public void reissueToken_ShouldCallMemberServiceReissueAccessToken() throws Exception {
        // Given
        Long memberId = 1L;
        TokenWithExpiration newAccessToken = new TokenWithExpiration("newAccessToken", 3600L);
        LoginResponse expectedResponse = LoginResponse.builder()
                .accessToken(newAccessToken)
                .build();

        when(memberService.reissueAccessToken(memberId)).thenReturn(expectedResponse);

        // When
        MvcResult result = mockMvc.perform(post("/members/reissue"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        verify(memberService).reissueAccessToken(memberId);
        String responseBody = result.getResponse().getContentAsString();
        LoginResponse actualResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        assertThat(actualResponse.getAccessToken().getToken()).isEqualTo("newAccessToken");
    }
}
