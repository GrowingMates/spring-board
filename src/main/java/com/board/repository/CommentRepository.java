package com.board.repository;

import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndIsDeletedFalse(Long id);

    Page<CommentEntity> findByArticleAndParentIsNullAndIsDeletedFalse(ArticleEntity article, Pageable pageable);

    Page<CommentEntity> findByParentAndIsDeletedFalse(CommentEntity parent, Pageable pageable);

    int countByParentAndIsDeletedFalse(CommentEntity parent);
}
