package com.board.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.comment.domain.Comment;
import com.board.comment.loader.CommentTestDataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import(CommentTestDataLoader.class)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("lastId보다 작은 ID를 가진 댓글을 내림차순으로 가져온다. (no-offset-paging)")
    void findAllByArticleId() {
        // given
        Long articleId = 1L;
        Long lastId = 3L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        // when
        Page<Comment> comments = commentRepository.findByArticleIdAndIdLessThanOrderByIdDesc(articleId, lastId, pageable);

        // then
        assertThat(comments).hasSize(2)
                .extracting(Comment::getContent)
                .containsExactly("첫 번째 게시글 댓글 2", "첫 번째 게시글 댓글 1");
    }

    @Test
    @DisplayName("멤버 아이디에 해당하는 모든 댓글을 조회한다.(offset-paging)")
    void findAllByMemberId() {
        // given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        // when
        Page<Comment> comments = commentRepository.findAllByMemberId(memberId, pageable);

        // then
        assertThat(comments).hasSize(4)
                .extracting(Comment::getMemberId)
                .containsExactly(1L, 1L, 1L, 1L);
    }
}
