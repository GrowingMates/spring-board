package com.board.member.controller;

import com.board.member.dto.LoginRequest;
import com.board.member.dto.LoginResponse;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void signUp_Success() throws Exception {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest("test@example.com", "nickname", "password123");
        MemberSignUpResponse response = new MemberSignUpResponse(1L, "test@example.com", "nickname");

        when(memberService.signUp(any(MemberSignUpRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/members/signup")
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
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"));
    }
}
