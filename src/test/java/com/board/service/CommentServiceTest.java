package com.board.service;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.dto.response.CommentResponse;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.repository.CommentRepository;
import com.exception.custom.DifferentOwnerException;
import com.exception.custom.MyEntityNotFoundException;
import com.exception.custom.NotIncludeBoardException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private MemberEntity testMember;
    private ArticleEntity testArticle;
    private CommentEntity testComment;
    private Long articleId = 1L;
    private Long memberId = 1L;
    private Long commentId = 3L;

    @BeforeEach
    void setUp() {
        testMember = new MemberEntity(memberId, "test@example.com", "password", "nickname");
        testArticle = new ArticleEntity(articleId, "제목", "내용", testMember, 0);
        testComment = new CommentEntity(commentId, "기존 댓글 내용", testArticle, testMember);
    }

    @Test
    @DisplayName("특정 게시글의 댓글 목록을 페이지 단위로 조회한다.")
    void 페이지_조회() {
        // Given
        MemberEntity member2 = new MemberEntity(2L, "member2@example.com", "password", "member2");
        CommentEntity comment2 = new CommentEntity(2L, "댓글내용2", testArticle, member2);
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentEntity> comments = List.of(testComment, comment2);
        Page<CommentEntity> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(commentRepository.findByArticleAndParentIsNullAndIsDeletedFalse(testArticle, pageable)).thenReturn(commentPage);

        // When
        Page<CommentResponse> result = commentService.findAllTopLevelComments(articleId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo(testComment.getContent());
        assertThat(result.getContent().get(0).getAuthorId()).isEqualTo(testMember.getId());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 댓글 목록을 조회하면 예외가 발생한다.")
    void 존재하지_않는_게시글_ID로_댓글_목록을_조회하면_예외가_발생() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(articleService.findById(articleId)).thenThrow(MyEntityNotFoundException.class);

        // When & Then
        assertThatThrownBy(() -> commentService.findAllTopLevelComments(articleId, pageable))
                .isInstanceOf(MyEntityNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 댓글을 생성하고 저장한다.")
    void 새로운_댓글을_생성하고_저장() {
        // Given
        String content = "새로운 댓글 내용";
        CommentCreateRequest request = new CommentCreateRequest(content);
        CommentEntity savedComment = new CommentEntity(2L, content, testArticle, testMember);

        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(memberService.findById(memberId)).thenReturn(testMember);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(savedComment);

        // When
        CommentEntity result = commentService.createComment(articleId, request, memberId);

        // Then
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getArticle().getId()).isEqualTo(articleId);
        assertThat(result.getMember().getId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글을 생성하려고 하면 예외가 발생한다.")
    void 존재하지_않는_게시글에_댓글을_생성하려고_하면_예외가_발생() {
        // Given
        CommentCreateRequest request = new CommentCreateRequest("새로운 댓글 내용");
        when(articleService.findById(articleId)).thenThrow(MyEntityNotFoundException.class);

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(articleId, request, memberId))
                .isInstanceOf(MyEntityNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 댓글을 생성하려고 하면 예외가 발생한다.")
    void 존재하지_않는_회원으로_댓글을_생성하려고_하면_예외가_발생() {
        // Given
        CommentCreateRequest request = new CommentCreateRequest("새로운 댓글 내용");
        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(memberService.findById(memberId)).thenThrow(MyEntityNotFoundException.class);

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(articleId, request, memberId))
                .isInstanceOf(MyEntityNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 내용을 수정한다.")
    void 댓글_내용을_수정() {
        // Given
        String updatedContent = "수정된 댓글 내용";
        CommentUpdateRequest request = new CommentUpdateRequest(updatedContent, commentId);

        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(testComment));
        when(memberService.findById(memberId)).thenReturn(testMember);

        // When
        CommentEntity result = commentService.updateComment(articleId, commentId, request, memberId);

        // Then
        assertThat(result.getContent()).isEqualTo(updatedContent);
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 수정하려고 하면 예외가 발생한다.")
    void 존재하지_않는_댓글을_수정하려고_하면_예외가_발생() {
        // Given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용", commentId);
        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.updateComment(articleId, commentId, request, memberId))
                .isInstanceOf(MyEntityNotFoundException.class);
    }

    @Test
    @DisplayName("수정하려는 댓글이 해당 게시글에 속하지 않으면 예외가 발생한다.")
    void 수정하려는_댓글이_해당_게시글에_속하지_않으면_예외가_발생() {

        ArticleEntity anotherArticle = new ArticleEntity(999L, "제목", "내용", testMember, 0);
        CommentEntity anotherComment = new CommentEntity(commentId, "기존 댓글 내용", anotherArticle, testMember);

        // Given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용", commentId);

        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(anotherComment));

        // When & Then
        assertThatThrownBy(() -> commentService.updateComment(articleId, commentId, request, memberId))
                .isInstanceOf(NotIncludeBoardException.class);
    }

    @Test
    @DisplayName("댓글을 soft delete 한다.")
    void 댓글을_soft_delete_한다() {
        // Given
        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(testComment));
        when(memberService.findById(memberId)).thenReturn(testMember);

        // When
        commentService.deleteComment(articleId, commentId, memberId);

        // Then
        assertThatCode(() -> commentService.deleteComment(articleId, commentId, memberId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 삭제하려고 하면 예외가 발생한다.")
    void 존재하지_않는_댓글을_삭제하려고_하면_예외가_발생() {
        // given
        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(articleId, commentId, memberId))
                .isInstanceOf(MyEntityNotFoundException.class);
    }

    @Test
    @DisplayName("삭제하려는 댓글이 해당 게시글에 속하지 않으면 예외가 발생한다.")
    void 삭제하려는_댓글이_해당_게시글에_속하지_않으면_예외가_발생() {
        // Given
        ArticleEntity anotherArticle = new ArticleEntity(999L, "제목", "내용", testMember, 0);
        CommentEntity anotherComment = new CommentEntity(commentId, "기존 댓글 내용", anotherArticle, testMember);

        // when
        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(anotherComment));

        // then
        assertThatThrownBy(() -> commentService.deleteComment(articleId, commentId, memberId))
                .isInstanceOf(NotIncludeBoardException.class);
    }

    @Test
    @DisplayName("삭제 권한이 없는 사용자가 댓글을 삭제하려고 하면 예외가 발생한다.")
    void 삭제_권한이_없는_사용자가_댓글을_삭제하려고_하면_예외가_발생() {
        // given
        MemberEntity anotherMember = new MemberEntity(999L, "not-owner@example.com", "pw", "nick");

        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(testComment));
        when(memberService.findById(anotherMember.getId())).thenReturn(anotherMember);

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(articleId, commentId, anotherMember.getId()))
                .isInstanceOf(DifferentOwnerException.class);
    }

    @Test
    @DisplayName("수정 권한이 없는 사용자가 댓글을 수정하려고 하면 예외가 발생한다.")
    void 수정_권한이_없는_사용자가_댓글을_수정하려고_하면_예외가_발생() {
        // given
        MemberEntity anotherMember = new MemberEntity(999L, "not-owner@example.com", "pw", "nick");

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용", commentId);
        when(commentRepository.findByIdAndIsDeletedFalse(commentId)).thenReturn(Optional.of(testComment));
        when(memberService.findById(anotherMember.getId())).thenReturn(anotherMember);

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(articleId, commentId, request, anotherMember.getId()))
                .isInstanceOf(DifferentOwnerException.class);
    }

    @Test
    @DisplayName("전체 댓글 조회 시 각 댓글의 대댓글 수를 정확히 조회한다.")
    void 전체댓글조회시_대댓글수_정확조회() {
        // Given
        CommentEntity reply1 = new CommentEntity(10L, "대댓글1", testArticle, testMember, testComment);
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentEntity> comments = List.of(testComment);
        Page<CommentEntity> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(commentRepository.findByArticleAndParentIsNullAndIsDeletedFalse(testArticle, pageable)).thenReturn(commentPage);
        when(commentRepository.countByParentAndIsDeletedFalse(testComment)).thenReturn(5);

        // When
        Page<CommentResponse> result = commentService.findAllTopLevelComments(articleId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReplyCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("댓글의 대댓글 수를 정확히 조회한다.")
    void 대댓글수_정확조회() {
        // Given
        when(commentRepository.countByParentAndIsDeletedFalse(testComment)).thenReturn(3);

        // When
        int replyCount = commentService.getReplyCount(testComment);

        // Then
        assertThat(replyCount).isEqualTo(3);
    }

    @Test
    @DisplayName("대댓글에 대댓글을 작성하려고 하면 예외가 발생한다.")
    void 대댓글에_대댓글작성_예외() {
        // Given
        CommentEntity parentReply = new CommentEntity(99L, "부모 대댓글", testArticle, testMember, testComment);

        CommentCreateRequest request = new CommentCreateRequest("대댓글의 대댓글", parentReply.getId());
        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(memberService.findById(memberId)).thenReturn(testMember);
        when(commentRepository.findByIdAndIsDeletedFalse(parentReply.getId())).thenReturn(Optional.of(parentReply));

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(articleId, request, memberId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("댓글에 대댓글을 작성하고, 정상적으로 저장된다.")
    void 대댓글_작성_성공() {
        // Given
        CommentCreateRequest request = new CommentCreateRequest("대댓글 내용", testComment.getId());
        CommentEntity reply = new CommentEntity(20L, "대댓글 내용", testArticle, testMember, testComment);

        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(memberService.findById(memberId)).thenReturn(testMember);
        when(commentRepository.findByIdAndIsDeletedFalse(testComment.getId())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(reply);

        // When
        CommentEntity result = commentService.createComment(articleId, request, memberId);

        // Then
        assertThat(result.getContent()).isEqualTo("대댓글 내용");
        assertThat(result.getParent()).isEqualTo(testComment);
    }

    @Test
    @DisplayName("부모 댓글이 다른 게시글에 속하면 예외가 발생한다.")
    void 부모댓글이_다른게시글이면_예외() {
        // Given
        ArticleEntity otherArticle = new ArticleEntity(2L, "다른 글", "내용", testMember, 0);
        CommentEntity otherComment = new CommentEntity(200L, "다른 글의 댓글", otherArticle, testMember);
        CommentCreateRequest request = new CommentCreateRequest("잘못된 대댓글", otherComment.getId());

        when(articleService.findById(articleId)).thenReturn(testArticle);
        when(memberService.findById(memberId)).thenReturn(testMember);
        when(commentRepository.findByIdAndIsDeletedFalse(otherComment.getId())).thenReturn(Optional.of(otherComment));

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(articleId, request, memberId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("대댓글 목록을 페이지 단위로 조회한다.")
    void 대댓글_리스트_조회() {
        // Given
        CommentEntity reply1 = new CommentEntity(301L, "대댓글1", testArticle, testMember, testComment);
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> replyPage = new PageImpl<>(List.of(reply1), pageable, 1);

        when(commentRepository.findByIdAndIsDeletedFalse(testComment.getId())).thenReturn(Optional.of(testComment));
        when(commentRepository.findByParentAndIsDeletedFalse(testComment, pageable)).thenReturn(replyPage);

        // When
        Page<CommentEntity> result = commentService.findReplies(testComment.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("대댓글1");
    }
}
