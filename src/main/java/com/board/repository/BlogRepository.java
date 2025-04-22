package com.board.repository;

import com.board.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<ArticleEntity, Long> {
}
