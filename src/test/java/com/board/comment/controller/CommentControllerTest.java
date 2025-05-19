package com.board.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.comment.controller.dto.request.CommentRequest;
import com.board.comment.domain.Comment;
import com.board.comment.service.CommentService;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthArgumentResolver authArgumentResolver;

    private Comment response;
    private Page<Comment> commentPage;
    @BeforeEach
    void setUp() throws Exception {
        response = new Comment(1L, 1L, "댓글 내용");
        List<Comment> responses = List.of(response);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        commentPage = new PageImpl<>(responses, pageable, responses.size());

        given(authArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("댓글을 생성한다.")
    void createComment() throws Exception {
        // given
        CommentRequest request = new CommentRequest("댓글 내용");
        given(commentService.createComment(any(), any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/articles/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("댓글 내용"));
    }

    @Test
    @DisplayName("게시글의 댓글들을 조회한다.")
    void showArticleComments() throws Exception {
        // given
        Long lastId = 3L;
        int size = 5;
        given(commentService.findArticleComments(1L, lastId, size)).willReturn(commentPage);

        // when & then
        mockMvc.perform(get("/articles/1/comments")
                        .param("lastId", String.valueOf(lastId))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentResponses[0].content").value("댓글 내용"));
    }

    @Test
    @DisplayName("회원이 작성한 댓글들을 조회한다.")
    void showMemberComments() throws Exception {
        // given
        int page = 0;
        int size = 10;
        given(commentService.findMemberComments(1L, page, size)).willReturn(commentPage);

        // when & then
        mockMvc.perform(get("/members/me/comments")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentResponses[0].content").value("댓글 내용"));
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateComment() throws Exception {
        // given
        CommentRequest request = new CommentRequest("수정된 댓글");
        Comment updatedResponse = new Comment(1L, 1L,"수정된 댓글");
        given(commentService.updateComment(any(), any(), any())).willReturn(updatedResponse);

        // when & then
        mockMvc.perform(patch("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 댓글"));
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteComment() throws Exception {
        // given
        given(commentService.deleteComment(1L, 1L)).willReturn(response);

        // when & then
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("댓글 내용"));
    }
}
