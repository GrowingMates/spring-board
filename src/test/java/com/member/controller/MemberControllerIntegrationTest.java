package com.member.controller;

import com.board.dto.request.ArticleCreateRequest;
import com.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.dto.request.LoginRequest;
import com.member.dto.response.LoginResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
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
    Long setUpMemberId;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 데이터 미리 저장
        MemberEntity member = new MemberEntity(setUpMemberEmail, setUpMemberPassword, setUpMemberNickname);
        MemberEntity save = memberRepository.save(member);
        setUpMemberId = save.getId();
    }

    @Test
    @DisplayName("로그아웃_요청시_쿠키가_만료된다")
    void 로그아웃_요청시_쿠키가_만료된다() throws Exception {
        // Given
        String token = getAccessToken();

        // When: 로그아웃 요청
        mockMvc.perform(post("/members/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원탈퇴_요청시_쿠키가_만료되고_재로그인이_불가능하다")
    void 회원탈퇴_후_재로그인_불가() throws Exception {
        // Given
        String token = getAccessToken();

        // When: 회원 탈퇴 요청
        mockMvc.perform(delete("/members/withdraw")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Then: 로그인 실패
        LoginRequest loginRequest = new LoginRequest(setUpMemberEmail, setUpMemberPassword);
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/public/members/login")
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
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

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
                        .header("Authorization", "Bearer " + token))
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

        MvcResult result = mockMvc.perform(post("/public/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody)
                .get("accessToken")
                .get("token")
                .asText(); // JWT 토큰 쿠키 값 반환
    }

    @Test
    @DisplayName("만료된_엑세스_토큰으로_요청시_401_응답")
    void 만료된_엑세스_토큰으로_요청시_401_응답() throws Exception {
        // Given: 3초짜리 만료 토큰 생성
        String expiredSoonToken = jwtUtil.generateToken(setUpMemberId, 1); // 3초

        // 3초 대기
        Thread.sleep(5);

        // When: 인증 필요한 요청 시도
        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + expiredSoonToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ArticleCreateRequest("제목", "내용"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("만료된_엑세스_토큰으로_재발급_요청시_401_응답")
    void 만료된_엑세스_토큰으로_재발급_요청시_401_응답() throws Exception {
        // Given
        String expiredSoonToken = jwtUtil.generateToken(setUpMemberId, 1); // 3초 뒤 만료되는 토큰 생성
        Thread.sleep(5);

        // When
        mockMvc.perform(post("/members/reissue")
                        .header("Authorization", "Bearer " + expiredSoonToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효한_엑세스_토큰으로_재발급_요청시_새로운_엑세스_토큰_반환")
    void 유효한_엑세스_토큰으로_재발급_요청시_성공() throws Exception {
        // Given
        String validToken = getAccessToken();

        // When
        MvcResult result = mockMvc.perform(post("/members/reissue")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        assertThat(loginResponse.getAccessToken()).isNotNull();
        assertThat(loginResponse.getAccessToken().getToken()).isNotBlank();
        assertThat(loginResponse.getAccessToken().getExpiration()).isGreaterThan(0L);
    }
}
