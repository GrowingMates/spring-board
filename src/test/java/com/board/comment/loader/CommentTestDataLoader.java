package com.board.comment.loader;

import com.board.comment.domain.Comment;
import com.board.comment.repository.CommentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CommentTestDataLoader {

    private final CommentRepository repository;

    public CommentTestDataLoader(CommentRepository repository) {
        this.repository = repository;
    }

    @Bean
    public CommandLineRunner testData() {
        return args -> {
            repository.save(new Comment(1L, 1L, "첫 번째 게시글 댓글 1"));
            repository.save(new Comment(1L, 1L, "첫 번째 게시글 댓글 2"));
            repository.save(new Comment(1L, 2L, "두 번째 게시글 댓글 1"));
            repository.save(new Comment(1L, 2L, "두 번째 게시글 댓글 2"));
        };
    }
}
