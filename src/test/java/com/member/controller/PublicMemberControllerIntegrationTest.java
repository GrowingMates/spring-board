package com.member.controller;

import com.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import com.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class PublicMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환을 위해 사용

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    JwtUtil jwtUtil;

    String setUpMemberEmail = "setupMember@example.com";
    String setUpMemberPassword = "12345";
    String setUpMemberNickname = "setupMemberNickname";

    @BeforeEach
    void setUp() {
        // 테스트용 회원 데이터 미리 저장
        MemberEntity member = new MemberEntity(setUpMemberEmail, setUpMemberPassword, setUpMemberNickname);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/public/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()); // accessToken 존재 확인
    }

    @Test
    @DisplayName("로그인 후 JWT 발행 및 검증")
    void loginAndValidateJwtTest() throws Exception {

        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult loginResult = mockMvc.perform(post("/public/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        // JWT 추출 및 검증
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo(setUpMemberEmail);
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signUpTest() throws Exception {
        final String testEmail = "test@example.com";
        final String testNickName = "test-nickname";
        final String testPassword = "password123";

        SignUpRequest signUpRequest = new SignUpRequest(testEmail, testNickName, testPassword);
        String signUpJson = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(post("/public/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.nickName").value(testNickName));
    }
}
