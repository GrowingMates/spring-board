package com.board.config;

import com.board.board.repository.BlogRepository;
import com.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
/* 테스트에 방해되서 주석처리
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 기본 회원 데이터 생성
            MemberEntity member1 = MemberEntity.builder()
                    .email("aa@aa.com")
                    .password("1234")
                    .nickName("aa")
                    .build();

            MemberEntity member2 = MemberEntity.builder()
                    .email("aa@aa.com2")
                    .password("1234")
                    .nickName("aa2")
                    .build();

            memberRepository.save(member1);
            memberRepository.save(member2);

            // 기본 게시글 데이터 생성
            Article article1 = Article.builder()
                    .title("First Article")
                    .content("Content of the first article")
                    .member(member1)
                    .build();

            Article article2 = Article.builder()
                    .title("Second Article")
                    .content("Content of the second article")
                    .member(member1)
                    .build();

            Article article3 = Article.builder()
                    .title("Third Article")
                    .content("Content of the third article")
                    .member(member2)
                    .build();

            blogRepository.save(article1);
            blogRepository.save(article2);
            blogRepository.save(article3);
        };
    }*/
}
