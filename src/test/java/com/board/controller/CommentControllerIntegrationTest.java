package com.board.controller;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.repository.ArticleRepository;
import com.board.repository.CommentRepository;
import com.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import com.support.CleanDatabaseBeforeEachTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerIntegrationTest extends CleanDatabaseBeforeEachTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private MemberEntity member;
    private ArticleEntity article;
    private String jwtToken;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @BeforeEach
    void setup() {
        member = memberRepository.save(new MemberEntity("test@example.com", "password", "nickname"));
        article = articleRepository.save(new ArticleEntity("title", "content", member));
        jwtToken = "Bearer " + jwtUtil.generateToken(member.getId(), 1000 * 60 * 60);
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void 댓글_생성_성공() throws Exception {
        // Given
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

        // When & Then
        mockMvc.perform(post("/articles/" + article.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION_HEADER, jwtToken)) // 가짜 인증 헤더
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("댓글 내용"))
                .andExpect(jsonPath("$.authorName").value("nickname"));
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void 댓글_조회_성공() throws Exception {
        // Given
        CommentEntity comment1 = commentRepository.save(new CommentEntity("댓글 내용1", article, member));
        CommentEntity comment2 = commentRepository.save(new CommentEntity("댓글 내용2", article, member));

        // When & Then
        mockMvc.perform(get("/articles/" + article.getId() + "/comments")
                        .param("articleId", String.valueOf(article.getId()))
                        .param("page", "0")
                        .param("size", "10")
                        .header(AUTHORIZATION_HEADER, jwtToken)) // JWT 인증 헤더 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("댓글 내용2"))
                .andExpect(jsonPath("$.content[1].content").value("댓글 내용1"));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void 댓글_수정_성공() throws Exception {
        // Given
        CommentEntity comment = commentRepository.save(new CommentEntity("댓글 내용", article, member));
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용", comment.getId());

        // When & Then
        mockMvc.perform(patch("/articles/" + article.getId() + "/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION_HEADER, jwtToken)) // JWT 인증 헤더 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용"))
                .andExpect(jsonPath("$.authorName").value(member.getNickName()));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() throws Exception {
        // Given
        CommentEntity comment = commentRepository.save(new CommentEntity("댓글 내용", article, member));

        // When & Then
        mockMvc.perform(delete("/articles/" + article.getId() + "/comments/" + comment.getId())
                        .header(AUTHORIZATION_HEADER, jwtToken)) // JWT 인증 헤더 추가
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("대댓글_목록_조회_성공")
    void 대댓글_목록_조회_성공() throws Exception {
        // Given
        CommentEntity comment = commentRepository.save(new CommentEntity("부모 댓글", article, member));
        CommentEntity childComment1 = commentRepository.save(CommentEntity.builder()
                .content("대댓글 1")
                .article(article)
                .member(member)
                .parent(comment)
                .build());
        CommentEntity childComment2 = commentRepository.save(CommentEntity.builder()
                .content("대댓글 2")
                .article(article)
                .member(member)
                .parent(comment)
                .build());

        // When & Then
        mockMvc.perform(get("/articles/{articleId}/comments/{commentId}/replies", article.getId(), comment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("대댓글 1"))
                .andExpect(jsonPath("$.content[1].content").value("대댓글 2"));
    }
}
