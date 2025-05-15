package com.board.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.service.CommentService;
import com.config.auth.AuthenticatedMemberArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.entity.MemberEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long articleId = 1L;
    private final Long commentId = 10L;
    private final Long memberId = 100L;

    @BeforeEach
    void setup() throws Exception {
        when(authenticatedMemberArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authenticatedMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(memberId); // @AuthenticatedMember 역할
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void 댓글_생성() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");
        MemberEntity member = new MemberEntity("이메일", "패스워드", "닉네임");
        CommentEntity comment = new CommentEntity("댓글 내용", null, member);

        when(commentService.createComment(eq(articleId), any(CommentCreateRequest.class), eq(memberId)))
                .thenReturn(comment);

        // when & then
        mockMvc.perform(post("/articles/{articleId}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("댓글 내용"))
                .andExpect(jsonPath("$.authorName").value("닉네임"));
    }

    @Test
    @DisplayName("댓글 전체 조회 성공")
    void 댓글_전체_조회_성공() throws Exception {
        // given
        MemberEntity member = new MemberEntity("email", "password", "nickName");
        ArticleEntity article = new ArticleEntity("title", "content", member);
        List<CommentEntity> comments = List.of(
                new CommentEntity("내용1", article, member),
                new CommentEntity("내용2", article, member));
        Page<CommentEntity> pageResult = new PageImpl<>(comments);

        when(commentService.findAllComments(eq(articleId), any())).thenReturn(pageResult);

        // when & then
        mockMvc.perform(get("/articles/{articleId}/comments", articleId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("내용1"))
                .andExpect(jsonPath("$.content[1].authorName").value("nickName"));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void 댓글_수정_성공() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용", commentId);
        MemberEntity member = new MemberEntity("email", "password", "nickName");
        CommentEntity updatedComment = new CommentEntity("수정된 댓글 내용", null, member);

        when(commentService.updateComment(eq(articleId), eq(commentId), any(CommentUpdateRequest.class), eq(memberId)))
                .thenReturn(updatedComment);

        // when & then
        mockMvc.perform(patch("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용"))
                .andExpect(jsonPath("$.authorName").value("nickName"));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() throws Exception {
        mockMvc.perform(delete("/articles/{articleId}/comments/{commentId}", articleId, commentId))
                .andExpect(status().isNoContent());
    }
}
