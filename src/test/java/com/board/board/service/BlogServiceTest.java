package com.board.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.repository.BlogRepository;
import com.board.config.auth.AuthUtil;
import com.board.exception.custom.DifferentOwnerException;
import com.board.exception.custom.MyEntityNotFoundException;
import com.board.member.entity.MemberEntity;
import com.board.member.service.MemberService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @InjectMocks
    private BlogService blogService;

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private AuthUtil authUtil;

    @Test
    @DisplayName("Serivce - saveArticle - 성공")
    void saveArticle_Success() {

        // Given
        String email = "test@example.com";
        String password = "1234";
        String nickName = "cc";
        MemberEntity member = new MemberEntity(email, password, nickName);
        ArticleCreateRequest request = new ArticleCreateRequest("title", "content");
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();

        when(authUtil.getMemberEmail()).thenReturn(email);
        when(memberService.findByEmail(email)).thenReturn(member);
        when(blogRepository.save(any(Article.class))).thenReturn(article);

        // When
        Article savedArticle = blogService.save(request);

        // Then
        assertNotNull(savedArticle);
        assertEquals("title", savedArticle.getTitle());
        assertEquals("content", savedArticle.getContent());
        assertEquals(member, savedArticle.getMember());
    }

    @Test
    @DisplayName("Serivce - findAll - 성공")
    void findAllArticles_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Article> mockPage = new PageImpl<>(Collections.emptyList());

        when(blogRepository.findAll(pageable)).thenReturn(mockPage);

        // When
        Page<Article> result = blogService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Serivce - findById - 성공")
    void findById_ArticleExists() {
        // Given
        Article article = new Article(1L, "title", "content", new MemberEntity());
        when(blogRepository.findById(1L)).thenReturn(Optional.of(article));

        // When
        Article foundArticle = blogService.findById(1L);

        // Then
        assertNotNull(foundArticle);
        assertEquals(1L, foundArticle.getId());
        assertEquals("title", foundArticle.getTitle());
    }

    @Test
    @DisplayName("Serivce - 없는 정보 조회 시 에러 발생")
    void findById_ArticleNotFound() {
        // Given
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MyEntityNotFoundException.class, () -> blogService.findById(1L));
    }

    @Test
    @DisplayName("Serivce - delete 성공")
    void deleteArticle_Success() {
        // Given
        String email = "test@example.com";
        MemberEntity member = new MemberEntity(email, "testUser", "nickName");
        Article article = new Article(1L, "title", "content", member);

        when(authUtil.getMemberEmail()).thenReturn(email);
        when(memberService.findByEmail(email)).thenReturn(member);
        when(blogRepository.findById(1L)).thenReturn(Optional.of(article));

        // When
        blogService.delete(1L);

        // Then
        verify(blogRepository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("Serivce - 다른 사람 게시글 삭제 시 에러 발생")
    void deleteArticle_NotAuthor_ThrowsException() {
        // Given
        String email = "test@example.com";
        MemberEntity member = new MemberEntity(1L, email, "1234", "testUser");
        MemberEntity anotherMember = new MemberEntity(2L, "other@example.com", "1234", "otherUser");
        Article article = new Article(1L, "title", "content", anotherMember);

        when(authUtil.getMemberEmail()).thenReturn(email);
        when(memberService.findByEmail(email)).thenReturn(member);
        when(blogRepository.findById(1L)).thenReturn(Optional.of(article));

        // When & Then
        assertThrows(DifferentOwnerException.class, () -> blogService.delete(1L));
    }
}
