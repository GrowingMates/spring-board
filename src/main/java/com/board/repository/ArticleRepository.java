package com.board.repository;

import com.board.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    Page<ArticleEntity> findAllByIsDeletedFalse(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ArticleEntity a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void increaseViewCount(@Param("id") Long id);
}
