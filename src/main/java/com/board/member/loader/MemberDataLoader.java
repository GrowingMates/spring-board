package com.board.member.loader;

import com.board.member.domain.member.Member;
import com.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDataLoader implements CommandLineRunner {

    private final MemberRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new Member("신짱구", "짱구", "aaa", "password123"));
        repository.save(new Member("신짱아", "짱아", "sss", "password456"));
    }
}
