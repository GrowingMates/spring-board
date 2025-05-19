package com.board.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.board.comment.controller.dto.request.CommentRequest;
import com.board.comment.domain.Comment;
import com.board.comment.exception.CommentErrorCode;
import com.board.comment.exception.CommentException;
import com.board.comment.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private List<Comment> comments;
    private Page<Comment> commentPage;

    @BeforeEach
    void set() {
        comment = new Comment(1L, 1L, "첫 번째 게시글 댓글 1");
        comments = List.of(comment);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        commentPage = new PageImpl<>(comments, pageable, comments.size());
    }

    @Nested
    class 정상_동작_테스트를_진행한다 {

        @Test
        @DisplayName("댓글을 저장한다.")
        void createComment() {
            // given
            CommentRequest request = new CommentRequest("첫 번째 게시글 댓글1");
            Long memberId = 1L;
            Long articleId = 1L;

            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            Comment response = commentService.createComment(request.content(), memberId, articleId);

            // then
            assertThat(response)
                    .extracting(Comment::getContent, Comment::getMemberId, Comment::getArticleId)
                    .containsExactly("첫 번째 게시글 댓글1", memberId, articleId);
        }

        @Test
        @DisplayName("게시글에 해당하는 모든 댓글을 조회한다.")
        void showArticleComments() {
            // given
            Long articleId = 1L;
            Long lastId = 0L;
            int size = 5;
            Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
            given(commentRepository.findByArticleIdAndIdLessThanOrderByIdDesc(articleId, lastId, pageable))
                    .willReturn(commentPage);

            // when
            Page<Comment> responses = commentService.findArticleComments(articleId, lastId, size);

            // then
            assertThat(responses).hasSize(1)
                    .extracting(Comment::getContent)
                    .containsExactly("첫 번째 게시글 댓글 1");
        }

        @Test
        @DisplayName("유저 아이디에 해당하는 모든 댓글을 조회한다.")
        void showMemberArticles() {
            // given
            Long memberId = 1L;
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
            given(commentRepository.findAllByMemberId(memberId, pageable)).willReturn(commentPage);

            // when
            Page<Comment> responses = commentService.findMemberComments(memberId, page, size);

            // then
            assertThat(responses).hasSize(1)
                    .extracting(Comment::getContent)
                    .containsExactly("첫 번째 게시글 댓글 1");
        }

        @Test
        @DisplayName("댓글을 업데이트 한다.")
        void updateComment() {
            // given
            Long commentId = 1L;
            Long memberId = 1L;
            CommentRequest request = new CommentRequest("수정된 게시글");
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when
            Comment response = commentService.updateComment(request, memberId, commentId);

            // then
            assertThat(response.getContent()).isEqualTo("수정된 게시글");
        }

        @Test
        @DisplayName("댓글을 삭제한다.")
        void deleteComment() {
            // given
            Long memberId = 1L;
            Long commentId = 1L;
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when
            Comment response = commentService.deleteComment(memberId, commentId);

            // then
            assertThat(response.getContent()).isEqualTo("첫 번째 게시글 댓글 1");
        }

        @Test
        @DisplayName("특정 댓글을 조회한다.")
        void getComment() {
            // given
            Long commentId = 1L;
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when
            Comment response = commentService.findComment(commentId);

            // then
            assertThat(response.getContent()).isEqualTo("첫 번째 게시글 댓글 1");
        }
    }

    @Nested
    class 예외_처리_테스트를_진행한다 {

        @Test
        @DisplayName("존재하지 않는 댓글을 조회한다.")
        void getCommentException() {
            // given
            Long commentId = 3L;
            given(commentRepository.findById(commentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.findComment(commentId))
                    .isInstanceOf(CommentException.class)
                    .hasMessageContaining(CommentErrorCode.NOT_FOUND_COMMENT.message());
        }

        @Test
        @DisplayName("댓글 작성자가 아닌 유저가 댓글을 수정하려 할 경우 예외가 발생한다.")
        void forbiddenUpdateComment() {
            // given
            Long memberId = 2L;
            Long commentId = 1L;
            CommentRequest request = new CommentRequest("댓글 수정");
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(request, memberId, commentId))
                    .isInstanceOf(CommentException.class)
                    .hasMessageContaining(CommentErrorCode.FORBIDDEN_ACCESS_COMMENT.message());
        }

        @Test
        @DisplayName("댓글 작성자가 아닌 유저가 댓글을 삭제하려 할 경우 예외가 발생한다.")
        void forbiddenDeleteComment() {
            // given
            Long memberId = 2L;
            Long commentId = 1L;
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(memberId, commentId))
                    .isInstanceOf(CommentException.class)
                    .hasMessageContaining(CommentErrorCode.FORBIDDEN_ACCESS_COMMENT.message());
        }
    }
}
