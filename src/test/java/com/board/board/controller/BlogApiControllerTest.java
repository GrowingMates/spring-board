package com.board.board.controller;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.dto.ArticleResponse;
import com.board.board.dto.ArticleUpdateRequest;
import com.board.board.service.BlogService;
import com.board.member.entity.MemberEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(BlogApiController.class)
class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlogService blogService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void addArticle_Success() throws Exception {
        // Given
        ArticleCreateRequest request = new ArticleCreateRequest("Title", "Content");
        ArticleResponse response = new ArticleResponse(1L, "Title", "Content", 2L);
        MemberEntity member = new MemberEntity(); // Mock member entity

        when(blogService.save(any(ArticleCreateRequest.class))).thenReturn(Article.builder()
                .title(response.getTitle())
                .content(response.getContent())
                .member(member)
                .build());

        // When & Then
        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    @Test
    void findAllArticles_Success() throws Exception {
        // Given
        List<ArticleResponse> responses = List.of(
                new ArticleResponse(1L, "Title1", "Content1", 1L),
                new ArticleResponse(2L, "Title2", "Content2", 1L)
        );
        MemberEntity member = new MemberEntity();
        when(blogService.findAll(any())).thenReturn(new PageImpl<>(responses.stream().map(response -> Article.builder()
                .title(response.getTitle())
                .content(response.getContent())
                .member(member)
                .build()).toList()));

        // When & Then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].content").value("Content2"));
    }

    @Test
    void findArticle_Success() throws Exception {
        // Given
        ArticleResponse response = new ArticleResponse(1L, "Title", "Content", 1L);
        MemberEntity member = new MemberEntity();

        when(blogService.findById(1L)).thenReturn(Article.builder()
                .title(response.getTitle())
                .content(response.getContent())
                .member(member)
                .build());

        // When & Then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"));
    }


    @Test
    void deleteArticle_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/articles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateArticle_Success() throws Exception {
        // Given
        ArticleUpdateRequest request = new ArticleUpdateRequest("Updated Title", "Updated Content");
        ArticleResponse response = new ArticleResponse(1L, "Updated Title", "Updated Content", 1L);

        MemberEntity member = new MemberEntity();
        when(blogService.update(any(Long.class), any(ArticleUpdateRequest.class))).thenReturn(Article.builder()
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
