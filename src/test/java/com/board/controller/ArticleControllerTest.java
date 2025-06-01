package com.board.controller;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.dto.response.ArticleResponse;
import com.board.entity.ArticleEntity;
import com.board.service.ArticleService;
import com.board.service.cache.ArticleViewCountCacheService;
import com.config.auth.AuthenticatedMemberArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private ArticleViewCountCacheService viewCountCacheService;

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
    @DisplayName("게시글 생성 성공")
    void addArticle_Success() throws Exception {
        // Given
        ArticleCreateRequest request = new ArticleCreateRequest("Title", "Content");
        MemberEntity member = MemberEntity.builder()
                .email("abc@example.com")
                .password("abc")
                .nickName("abc")
                .build();

        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .member(member)
                .build();

        when(memberService.findById(any())).thenReturn(member);
        when(articleService.save(any(ArticleCreateRequest.class), any(Long.class))).thenReturn(article);

        // When & Then
        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    @Test
    @DisplayName("전체 게시글 조회 성공 - 내용 미포함")
    void 전체_게시글_조회_성공() throws Exception {
        // Given
        List<ArticleResponse> responses = List.of(
                new ArticleResponse(1L, "Title1", null, 1L),
                new ArticleResponse(2L, "Title2", null, 1L)
        );
        MemberEntity member = MemberEntity.builder()
                .email("abc@example.com")
                .password("abc")
                .nickName("abc")
                .build();
        when(articleService.findAll(any())).thenReturn(
                new PageImpl<>(responses.stream().map(response -> ArticleEntity.builder()
                        .id(response.getId())
                        .title(response.getTitle())
                        .content(response.getContent())
                        .member(member)
                        .build()).toList()));

        // When & Then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Title1"))
                .andExpect(jsonPath("$.content[0].content").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @DisplayName("개별 게시글 조회 성공 - 내용 포함")
    void 개별_게시글_조회_성공_내용_포함() throws Exception {
        // Given
        ArticleResponse response = new ArticleResponse(1L, "Title", "Content", 1L, 0);
        MemberEntity member = MemberEntity.builder()
                .email("abc@example.com")
                .password("abc")
                .nickName("abc")
                .build();

        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .title(response.getTitle())
                .content(response.getContent())
                .member(member)
                .viewCount(0L) // 초기 조회수 설정
                .build();

        when(articleService.findById(1L)).thenReturn(article);

        // When & Then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"));
    }


    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteArticle_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/articles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateArticle_Success() throws Exception {
        // Given
        ArticleUpdateRequest request = new ArticleUpdateRequest("Updated Title", "Updated Content");
        ArticleResponse response = new ArticleResponse(1L, "Updated Title", "Updated Content", 1L, 0);

        MemberEntity member = MemberEntity.builder()
                .email("abc@example.com")
                .password("abc")
                .nickName("abc")
                .build();
        when(articleService.update(any(Long.class), any(Long.class), any(ArticleUpdateRequest.class)))
                .thenReturn(ArticleEntity.builder()
                        .title(response.getTitle())
                        .content(response.getContent())
                        .member(member)
                        .build());

        // When & Then
        mockMvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }
}
