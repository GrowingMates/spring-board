package com.board.repository;

import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndIsDeletedFalse(Long id);

    Page<CommentEntity> findByArticleAndIsDeletedFalse(ArticleEntity article, Pageable pageable);
}
