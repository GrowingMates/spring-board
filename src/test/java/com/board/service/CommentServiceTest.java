package com.board.service;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.repository.CommentRepository;
import com.exception.custom.DifferentOwnerException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleService articleService;
    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("게시글의 댓글들 조회")
    void 댓글_조회_성공() {
        // Given
        MemberEntity member = new MemberEntity("test@example.com", "password", "nickname");
        MemberEntity member2 = new MemberEntity("member2@example.com", "password", "member2");
        MemberEntity member3 = new MemberEntity("member3@example.com", "password", "member3");
        ArticleEntity article = new ArticleEntity("제목", "내용", member);

        CommentEntity comment = new CommentEntity("댓글내용1", article, member);
        CommentEntity comment2 = new CommentEntity("댓글내용2", article, member2);
        CommentEntity comment3 = new CommentEntity("댓글내용3", article, member3);
        CommentEntity comment4 = new CommentEntity("댓글내용4", article, member3);

        Pageable pageable = PageRequest.of(0, 10);

        List<CommentEntity> commentEntityList = List.of(comment, comment2, comment3, comment4);
        Page<CommentEntity> commentPage = new PageImpl<>(commentEntityList, pageable, commentEntityList.size());

        when(commentRepository.findByArticleIdAndDeletedFalseOrderByCreatedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(commentPage);

        // When
        Page<CommentEntity> returnCommentsList = commentService.findAllComments(1L, pageable);

        // then
        assertEquals(4, returnCommentsList.getContent().size());
        assertEquals("댓글내용1", returnCommentsList.getContent().get(0).getContent());
        assertEquals(member3, returnCommentsList.getContent().get(3).getMember());
    }

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

        when(articleService.findById(articleId)).thenReturn(article);
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
    @DisplayName("댓글 삭제 테스트")
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
