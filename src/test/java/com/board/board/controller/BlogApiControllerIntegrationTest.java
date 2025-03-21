package com.board.board.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.dto.ArticleUpdateRequest;
import com.board.board.repository.BlogRepository;
import com.board.config.jwt.JwtUtil;
import com.board.member.dto.LoginRequest;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.entity.MemberEntity;
import com.board.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlogApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String MEMBER_EMAIL = "setupMember@example.com";
    private static final String MEMBER_PASSWORD = "12345";
    private static final String MEMBER_NICKNAME = "setupMemberNickname";
    private static final int JWT_EXPIRATION_TIME = 3600000;

    private String tokenCookie;

    @BeforeEach
    void setUp() throws Exception {
        signUpAndLogin();
    }

    private void signUpAndLogin() throws Exception {
        sendPostRequest("/members/signup", new MemberSignUpRequest(MEMBER_EMAIL, MEMBER_NICKNAME, MEMBER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.nickName").value(MEMBER_NICKNAME));

        MvcResult loginResult = sendPostRequest("/members/login", new LoginRequest(MEMBER_EMAIL, MEMBER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expirationTime").value(JWT_EXPIRATION_TIME))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    String token = objectMapper.readTree(responseBody).get("accessToken").asText();
                    assertThat(jwtUtil.extractEmail(token)).isEqualTo(MEMBER_EMAIL);
                    assertThat(jwtUtil.isTokenValid(token)).isTrue();
                }).andReturn();

        tokenCookie = loginResult.getResponse().getCookie("token").getValue();
        assertThat(jwtUtil.isTokenValid(tokenCookie)).isTrue();
        assertThat(jwtUtil.extractEmail(tokenCookie)).isEqualTo(MEMBER_EMAIL);
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
                .cookie(new Cookie("token", tokenCookie)));
    }


    @Test
    @DisplayName("전체 조회 테스트")
    void findAllArticleTest() throws Exception {
        MemberEntity member1 = createAndSaveMember("bb@aa.com", "nickname1");
        MemberEntity member2 = createAndSaveMember("cc@aa.com", "nickname2");

        blogRepository.save(new Article("Title 1", "Content 1", member1));
        blogRepository.save(new Article("Title 2", "Content 2", member2));

        mockMvc.perform(get("/articles").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    @DisplayName("개별 조회 테스트")
    void findArticleTest() throws Exception {
        MemberEntity member = createAndSaveMember("bb@aa.com", "nickname");
        Article article = blogRepository.save(new Article("Title 1", "Content 1", member));

        mockMvc.perform(get("/articles/" + article.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title 1"))
                .andExpect(jsonPath("$.content").value("Content 1"));
    }

    @Test
    @DisplayName("로그인 없이 글 삭제 시 401 에러 발생")
    void notLoginDeleteArticleTest() throws Exception {
        Article article = createAndSaveArticle("Title 1", "Content 1");

        mockMvc.perform(delete("/articles/" + article.getId()))
                .andExpect(status().isUnauthorized());

        assertTrue(blogRepository.findById(article.getId()).isPresent());
    }

    @Test
    @DisplayName("글 삭제 테스트")
    void deleteArticleTest() throws Exception {
        Long articleId = createArticleAndGetId("테스트 제목", "테스트 내용");

        mockMvc.perform(delete("/articles/" + articleId).cookie(new Cookie("token", tokenCookie)))
                .andExpect(status().isNoContent());

        assertFalse(blogRepository.findById(articleId).isPresent());
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
                .cookie(new Cookie("token", tokenCookie)));
    }

    private MemberEntity createAndSaveMember(String email, String nickname) {
        return memberRepository.save(new MemberEntity(email, MEMBER_PASSWORD, nickname));
    }

    private Article createAndSaveArticle(String title, String content) {
        return blogRepository.save(new Article(title, content, createAndSaveMember("bb@aa.com", "nickname")));
    }

    private Long createArticleAndGetId(String title, String content) throws Exception {
        MvcResult mvcResult = sendPostRequest("/articles", new ArticleCreateRequest(title, content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andReturn();

        return JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.id", Long.class);
    }
}
