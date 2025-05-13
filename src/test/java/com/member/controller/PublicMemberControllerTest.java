package com.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.config.auth.AuthUtil;
import com.config.auth.AuthenticatedMemberArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.SignUpResponse;
import com.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublicMemberController.class)
class PublicMemberControllerTest {

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
    void signUp_Success() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest("test@example.com", "nickname", "password123");
        SignUpResponse response = new SignUpResponse(1L, "test@example.com", "nickname");

        when(memberService.signUp(any(SignUpRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/public/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickName").value("nickname"));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        LoginResponse loginResponse = new LoginResponse("mockAccessToken", 3600L);

        when(memberService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/public/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"));
    }
}
