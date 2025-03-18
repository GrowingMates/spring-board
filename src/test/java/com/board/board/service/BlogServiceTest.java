package com.board.board.service;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.repository.BlogRepository;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BlogServiceTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 후 글 작성에 성공한다")
    void 회원가입_글_작성() {
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("testEmail@test.com", "testNickName", "1234");
        MemberSignUpResponse memberSignUpResponse = memberService.signUp(memberSignUpRequest);

        String title = "title1";
        String content = "content1";
        ArticleCreateRequest request = new ArticleCreateRequest(title, content, memberSignUpResponse.getId());
        Article result = blogService.save(request);
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(memberSignUpResponse.getId(), result.getMember().getId());
    }
}
