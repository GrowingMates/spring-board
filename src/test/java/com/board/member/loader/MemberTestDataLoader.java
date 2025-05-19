package com.board.member.loader;

import com.board.member.domain.member.Member;
import com.board.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MemberTestDataLoader {

    private final MemberRepository repository;

    MemberTestDataLoader(MemberRepository memberRepository) {
        this.repository = memberRepository;
    }

    @Bean
    public CommandLineRunner testData() {
        return args -> {
            repository.save(new Member("신짱구", "짱구", "aaa", "password123"));
            repository.save(new Member("신짱아", "짱아", "sss", "password456"));
        };
    }
}
