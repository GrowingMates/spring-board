package com.board.repository;

import com.board.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndIsDeletedFalse(Long id);

    Page<CommentEntity> findByArticleIdAndIsDeletedFalse(Long articleId, Pageable pageable);

}
