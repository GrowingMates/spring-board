package com.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.repository.CommentRepository;
import com.exception.custom.DifferentOwnerException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BlogService blogService;
    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("댓글 생성 성공")
    void 댓글_생성_성공() {
        // Given
        Long memberId = 1L;
        Long articleId = 2L;
        String content = "댓글 내용";
        CommentCreateRequest request = new CommentCreateRequest(content, articleId);

        MemberEntity member = new MemberEntity("test@example.com", "password", "nickname");
        ArticleEntity article = new ArticleEntity("제목", "내용", member);
        CommentEntity comment = new CommentEntity(content, article, member);

        when(blogService.findById(articleId)).thenReturn(article);
        when(memberService.findById(memberId)).thenReturn(member);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        // When
        CommentEntity createdComment = commentService.createComment(request, memberId);

        // Then
        assertNotNull(createdComment);
        assertEquals(content, createdComment.getContent());
        assertEquals(article, createdComment.getArticle());
        assertEquals(member, createdComment.getMember());
    }

    @Test
    @DisplayName("댓글_수정_성공")
    void 댓글_수정_성공() {
        // Given
        Long memberId = 1L;
        Long commentId = 3L;
        String updatedContent = "수정된 댓글 내용";
        CommentUpdateRequest request = new CommentUpdateRequest(updatedContent, commentId);

        MemberEntity member = new MemberEntity("test@example.com", "password", "nickname");
        ArticleEntity article = new ArticleEntity("제목", "내용", member);
        CommentEntity comment = new CommentEntity("기존 댓글 내용", article, member);

        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(memberService.findById(memberId)).thenReturn(member);

        // When
        CommentEntity updatedComment = commentService.updateComment(request, memberId);

        // Then
        assertNotNull(updatedComment);
        assertEquals(updatedContent, updatedComment.getContent());
    }


    @Nested
    @DisplayName("삭제 테스트")
    class deleteTest {
        @Test
        @DisplayName("댓글_삭제_성공")
        void 댓글_삭제_성공() {
            // Given
            Long memberId = 1L;
            Long commentId = 3L;

            MemberEntity member = new MemberEntity("test@example.com", "password", "nickname");
            ArticleEntity article = new ArticleEntity("제목", "내용", member);
            CommentEntity comment = new CommentEntity("기존 댓글 내용", article, member);

            when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
            when(memberService.findById(memberId)).thenReturn(member);

            // When
            commentService.deleteComment(commentId, memberId);

            // Then
            assertEquals(true, comment.isDeleted());
        }

        @Test
        @DisplayName("작성자 다를 경우 삭제 실패")
        void 작성자_다르면_댓글_삭제_실패() {
            // Given
            Long memberId = 1L;
            Long commentId = 3L;

            MemberEntity member = new MemberEntity("test@example.com", "password", "nickname");
            MemberEntity anotherMember = new MemberEntity("another@example.com", "anotherPw", "another");
            ArticleEntity article = new ArticleEntity("제목", "내용", member);
            CommentEntity comment = new CommentEntity("기존 댓글 내용", article, anotherMember);

            when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
            when(memberService.findById(memberId)).thenReturn(member);

            // When & Then
            assertThrows(DifferentOwnerException.class, () -> commentService.deleteComment(commentId, memberId));
        }
    }

}
