package com.board.comment.loader;

import com.board.comment.domain.Comment;
import com.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDataLoader implements CommandLineRunner {

    private final CommentRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new Comment(1L, 1L, "첫 번째 게시글 댓글 1"));
        repository.save(new Comment(1L, 1L, "첫 번째 게시글 댓글 2"));
        repository.save(new Comment(1L, 2L, "두 번째 게시글 댓글 1"));
        repository.save(new Comment(1L, 2L, "두 번째 게시글 댓글 2"));
    }
}
