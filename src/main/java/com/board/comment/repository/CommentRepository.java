package com.board.comment.repository;

import com.board.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByArticleId(Long articleId);
    List<Comment> findAllByMemberId(Long memberId);
}
