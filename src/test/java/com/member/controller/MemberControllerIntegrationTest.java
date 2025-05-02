package com.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.dto.request.ArticleCreateRequest;
import com.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerIntegrationTest {

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

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token")) // JWT 쿠키 존재 확인
                .andExpect(jsonPath("$.accessToken").exists()); // accessToken 존재 확인
    }

    @Test
    @DisplayName("로그인 후 JWT 발행 및 검증")
    void loginAndValidateJwtTest() throws Exception {

        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult loginResult = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
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

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.nickName").value(testNickName));
    }

    @Test
    @DisplayName("로그아웃_요청시_쿠키가_만료된다")
    void 로그아웃_요청시_쿠키가_만료된다() throws Exception {
        // Given
        String token = getAccessToken();

        // When: 로그아웃 요청
        mockMvc.perform(post("/members/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("token", 0)); // Then: 쿠키 만료 확인
    }

    @Test
    @DisplayName("회원탈퇴_요청시_쿠키가_만료되고_재로그인이_불가능하다")
    void 회원탈퇴_후_재로그인_불가() throws Exception {
        // Given
        String token = getAccessToken();

        // When: 회원 탈퇴 요청
        mockMvc.perform(delete("/members/withdraw")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("token", 0));

        // Then: 로그인 실패
        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("로그아웃_후_인증이_필요한_요청시_거부된다")
    void 로그아웃_후_인증요청_실패() throws Exception {
        // Given
        String token = getAccessToken();

        // 로그아웃
        mockMvc.perform(post("/members/logout")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("token", 0)); // 쿠키가 만료되었는지 확인

        // 이후 요청 시 → 쿠키 없음 (즉, 인증 실패 유도)
        mockMvc.perform(post("/articles") // 인증 필요한 API
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ArticleCreateRequest("제목", "내용"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원탈퇴_후_인증이_필요한_요청시_거부된다")
    void 회원탈퇴_후_인증요청_실패() throws Exception {
        // Given: 로그인 후 JWT 토큰 발급
        String token = getAccessToken();

        // When: 회원 탈퇴 요청
        mockMvc.perform(delete("/members/withdraw")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());

        // Then: 인증이 필요한 요청 시 실패
        mockMvc.perform(post("/articles") // 인증 필요한 API
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ArticleCreateRequest("제목", "내용"))))
                .andExpect(status().isUnauthorized());
    }

    private String getAccessToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getCookie("token").getValue(); // JWT 토큰 쿠키 값 반환
    }

}
