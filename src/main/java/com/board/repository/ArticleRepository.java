package com.board.repository;

import com.board.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    Page<ArticleEntity> findAllByDeletedFalse(Pageable pageable);
}
