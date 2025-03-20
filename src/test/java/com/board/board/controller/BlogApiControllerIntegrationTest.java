package com.board.board.controller;

import com.board.board.dto.ArticleCreateRequest;
import com.board.board.service.BlogService;
import com.board.config.jwt.JwtUtil;
import com.board.member.dto.LoginRequest;
import com.board.member.dto.MemberSignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlogApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlogService blogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("회원가입 후 로그인 테스트")
    void signUpAndLoginTest() throws Exception {

        final String test_email = "test@example.com";
        final String test_nickName = "my-jwt-issuer"; // JWT 발행자 (설정값)
        final String test_password = "password123";

        MemberSignUpRequest signUpRequest = new MemberSignUpRequest(test_email, test_nickName, test_password);
        String signUpJson = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(test_email)) // 이메일 일치 검증
                .andExpect(jsonPath("$.nickName").value(test_nickName));

        // 2. 로그인 요청
        LoginRequest loginRequest = new LoginRequest(test_email, test_password);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        // 3. 로그인 후 응답에서 JWT 토큰 검증
        MvcResult loginResult = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token")) // JWT 쿠키 존재 여부 확인
                .andExpect(jsonPath("$.accessToken").exists()) // accessToken 존재 여부 확인
                .andExpect(jsonPath("$.accessToken").isNotEmpty()) // accessToken이 비어있지 않은지 확인
                .andExpect(jsonPath("$.expirationTime").value(3600000))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    String token = objectMapper.readTree(responseBody).get("accessToken").asText(); // accessToken 추출
                    assertThat(jwtUtil.extractEmail(token)).isEqualTo(test_email); // 토큰의 sub(이메일) 검증
                    assertThat(jwtUtil.isTokenValid(token)).isEqualTo(true); // 토큰의 issuer 검증
                }).andReturn();

        String tokenCookie = loginResult.getResponse().getCookie("token").getValue();
        assertThat(jwtUtil.isTokenValid(tokenCookie)).isTrue(); // 쿠키에 담긴 JWT가 정상적인지 검증
        assertThat(jwtUtil.extractEmail(tokenCookie)).isEqualTo(test_email); // JWT에서 email 추출 및 검증

        // 4️⃣ 글쓰기 요청 (JWT 쿠키 포함)
        ArticleCreateRequest articleCreateRequest = new ArticleCreateRequest("테스트 제목", "테스트 내용");
        String postJson = objectMapper.writeValueAsString(articleCreateRequest);

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson)
                        .cookie(new Cookie("token", tokenCookie))) // 쿠키를 포함하여 요청
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"));
    }
}
