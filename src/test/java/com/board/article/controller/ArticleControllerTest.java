package com.board.article.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.article.controller.dto.request.ArticleRequest;
import com.board.article.domain.Article;
import com.board.article.service.ArticleService;
import com.board.global.resolver.AuthArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private AuthArgumentResolver authArgumentResolver;

    private Article article;
    private Page<Article> articlePage;

    @BeforeEach
    void setUp() throws Exception {
        given(authArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);

        article = new Article(1L, "제목", "내용");
        ReflectionTestUtils.setField(article, "id", 1L);

        List<Article> articles = List.of(article);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        articlePage = new PageImpl<>(articles, pageable, articles.size());
    }

    @Test
    @DisplayName("게시글을 생성한다.")
    void createArticle() throws Exception {
        ArticleRequest request = new ArticleRequest("제목", "내용");
        given(articleService.createArticle(anyLong(), any(), any())).willReturn(article);

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/articles/1"))
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"));
    }

    @Test
    @DisplayName("모든 게시글을 조회한다.")
    void showAllArticles() throws Exception {
        given(articleService.findAllArticles(0L, 5)).willReturn(articlePage);

        mockMvc.perform(get("/articles")
                        .param("lastId", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleResponses[0].articleId").value(1L))
                .andExpect(jsonPath("$.articleResponses[0].title").value("제목"))
                .andExpect(jsonPath("$.articleResponses[0].content").value("내용"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("게시글 하나를 조회한다.")
    void showArticle() throws Exception {
        given(articleService.findArticle(1L)).willReturn(article);

        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"));
    }

    @Test
    @DisplayName("회원의 게시글을 조회한다.")
    void showMemberArticles() throws Exception {
        given(articleService.findMemberArticles(1L, 0, 10)).willReturn(articlePage);

        mockMvc.perform(get("/members/me/articles")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleResponses[0].articleId").value(1L))
                .andExpect(jsonPath("$.articleResponses[0].title").value("제목"))
                .andExpect(jsonPath("$.articleResponses[0].content").value("내용"));
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void updateArticle() throws Exception {
        ArticleRequest request = new ArticleRequest("수정된 제목", "수정된 내용");
        Article updatedArticle = new Article(1L, "수정된 제목", "수정된 내용");
        ReflectionTestUtils.setField(updatedArticle, "id", 1L);

        given(articleService.updateArticle(anyLong(), anyLong(), any(), any())).willReturn(updatedArticle);

        mockMvc.perform(patch("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteArticle() throws Exception {
        given(articleService.deleteArticle(1L, 1L)).willReturn(article);

        mockMvc.perform(delete("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"));
    }
}
