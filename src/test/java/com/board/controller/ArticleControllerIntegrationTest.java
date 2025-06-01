package com.board.controller;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.repository.ArticleRepository;
import com.board.service.cache.ArticleViewCountCacheService;
import com.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import com.support.CleanDatabaseBeforeEachTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleControllerIntegrationTest extends CleanDatabaseBeforeEachTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ArticleViewCountCacheService viewCountCacheService;

    @Value("${jwt.secret_key}")
    private String secretKey;

    private static final String MEMBER_EMAIL = "setupMember@example.com";
    private static final String MEMBER_PASSWORD = "12345";
    private static final String MEMBER_NICKNAME = "setupMemberNickname";
    private static final int JWT_EXPIRATION_TIME = 3600000;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        signUpAndLogin();
    }

    @Test
    void testJwtSecretKey() {
        System.out.println("[secret_key from test yml]: " + secretKey);
    }

    private void signUpAndLogin() throws Exception {
        sendPostRequest("/public/members/signup", new SignUpRequest(MEMBER_EMAIL, MEMBER_NICKNAME, MEMBER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.nickName").value(MEMBER_NICKNAME));

        sendPostRequest("/public/members/login",
                new LoginRequest(MEMBER_EMAIL, MEMBER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.token").exists())
                .andExpect(jsonPath("$.accessToken.token").isNotEmpty())
                .andExpect(jsonPath("$.accessToken.expiration").value(JWT_EXPIRATION_TIME))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    this.accessToken = objectMapper.readTree(responseBody).get("accessToken").get("token").asText();
                    Long memberId = jwtUtil.extractMemberIdFromToken(accessToken);
                    assertThat(jwtUtil.extractMemberIdFromToken(accessToken)).isEqualTo(memberId);
                    assertThat(jwtUtil.isTokenValid(accessToken)).isTrue();
                });
    }

    @Test
    @DisplayName("글 작성")
    void addArticleTest() throws Exception {
        sendPostRequest("/articles", new ArticleCreateRequest("테스트 제목", "테스트 내용"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"));
    }

    private ResultActions sendPostRequest(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + accessToken));
    }


    @Test
    @DisplayName("전체 조회 테스트")
    void findAllArticleTest() throws Exception {
        MemberEntity member1 = createAndSaveMember("bb@aa.com", "nickname1");
        MemberEntity member2 = createAndSaveMember("cc@aa.com", "nickname2");

        articleRepository.save(new ArticleEntity("Title 1", "Content 1", member1));
        articleRepository.save(new ArticleEntity("Title 2", "Content 2", member2));

        mockMvc.perform(get("/articles").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Title 2"))
                .andExpect(jsonPath("$.content[1].title").value("Title 1"));
    }

    @Test
    @DisplayName("삭제된 게시물을 제외하고 전체 조회 테스트")
    void findAllExcludingDeletedArticlesTest() throws Exception {
        // Given: 게시물 3개 생성
        MemberEntity member = createAndSaveMember("bb@aa.com", "nickname");
        ArticleEntity article1 = articleRepository.save(new ArticleEntity("Title 1", "Content 1", member));
        ArticleEntity article2 = articleRepository.save(new ArticleEntity("Title 2", "Content 2", member));
        ArticleEntity article3 = articleRepository.save(new ArticleEntity("Title 3", "Content 3", member));

        // 게시물 1개 삭제
        article3.softDelete();
        articleRepository.save(article3);

        // When: 전체 조회 요청
        mockMvc.perform(get("/articles").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2)) // 삭제된 게시물 제외
                .andExpect(jsonPath("$.content[0].title").value("Title 2"))
                .andExpect(jsonPath("$.content[1].title").value("Title 1"));
    }

    @Test
    @DisplayName("개별 조회 테스트")
    void findArticleTest() throws Exception {
        MemberEntity member = createAndSaveMember("bb@aa.com", "nickname");
        ArticleEntity article = articleRepository.save(new ArticleEntity("Title 1", "Content 1", member));

        mockMvc.perform(get("/articles/" + article.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title 1"))
                .andExpect(jsonPath("$.content").value("Content 1"));
    }

    @Test
    @DisplayName("글 상세조회시 조회수가 정상적으로 상승하면 성공")
    void 글_상세조회_조회수_상승() throws Exception {
        MemberEntity member = createAndSaveMember("bb@aa.com", "nickname");
        ArticleEntity article = articleRepository.save(new ArticleEntity("Title 1", "Content 1", member));

        mockMvc.perform(get("/articles/" + article.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title 1"))
                .andExpect(jsonPath("$.content").value("Content 1"))
                .andExpect(jsonPath("$.viewCount").value(1));

        mockMvc.perform(get("/articles/" + article.getId()))
                .andExpect(jsonPath("$.viewCount").value(2));
    }

    @Test
    @DisplayName("로그인 없이 글 삭제 시 401 에러 발생")
    void notLoginDeleteArticleTest() throws Exception {
        ArticleEntity article = createAndSaveArticle("Title 1", "Content 1");

        mockMvc.perform(delete("/articles/" + article.getId()))
                .andExpect(status().isUnauthorized());

        assertTrue(articleRepository.findById(article.getId()).isPresent());
    }

    @Test
    @DisplayName("글 삭제 테스트")
    void deleteArticleTest() throws Exception {
        Long articleId = createArticleAndGetId("테스트 제목", "테스트 내용");

        mockMvc.perform(delete("/articles/" + articleId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        ArticleEntity deletedArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new AssertionError("삭제된 글을 찾을 수 없습니다."));
        assertTrue(deletedArticle.isDeleted());
    }

    @Test
    @DisplayName("글 수정 테스트")
    void updateArticleTest() throws Exception {
        Long articleId = createArticleAndGetId("테스트 제목", "테스트 내용");

        sendPutRequest("/articles/" + articleId, new ArticleUpdateRequest("수정된 제목", "수정된 내용"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    private ResultActions sendPutRequest(String url, Object request) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + accessToken));
    }

    private MemberEntity createAndSaveMember(String email, String nickname) {
        return memberRepository.save(new MemberEntity(email, MEMBER_PASSWORD, nickname));
    }

    private ArticleEntity createAndSaveArticle(String title, String content) {
        return articleRepository.save(new ArticleEntity(title, content, createAndSaveMember("bb@aa.com", "nickname")));
    }

    private Long createArticleAndGetId(String title, String content) throws Exception {
        MvcResult mvcResult = sendPostRequest("/articles", new ArticleCreateRequest(title, content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andReturn();

        return JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.id", Long.class);
    }

    @Test
    @DisplayName("게시글 조회 시 Redis 캐시의 조회수가 증가한다")
    void 게시글_조회시_조회수_캐시_증가() throws Exception {
        // given
        MemberEntity member = createAndSaveMember("viewer@test.com", "viewer");
        ArticleEntity article = articleRepository.save(new ArticleEntity("조회수 테스트 제목", "조회수 테스트 내용", member));
        Long articleId = article.getId();

        // when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/articles/" + articleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("조회수 테스트 제목"));
        }

        // then
        long viewCount = viewCountCacheService.getViewCount(articleId);
        assertThat(viewCount).isEqualTo(3L);
    }

}
