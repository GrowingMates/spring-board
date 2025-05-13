package com.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.config.auth.AuthUtil;
import com.config.auth.AuthenticatedMemberArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    public void withdraw_ShouldCallMemberServiceWithdraw() throws Exception {
        // Given
        Long memberId = 1L;

        // When: 회원탈퇴 요청
        mockMvc.perform(delete("/members/withdraw"))
                .andExpect(status().isNoContent());

        // Then: MemberService의 withdraw 호출 확인
        verify(memberService).withdraw(memberId);
    }
}
