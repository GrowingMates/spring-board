package com.board.service;

import com.board.dto.request.ArticleCreateRequest;
import com.board.entity.ArticleEntity;
import com.board.repository.ArticleRepository;
import com.exception.custom.DifferentOwnerException;
import com.exception.custom.MyEntityNotFoundException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("Service - saveArticle - 성공")
    void saveArticle_Success() {
        // Given
        String email = "test@example.com";
        String password = "1234";
        String nickName = "cc";
        Long memberId = 1L;

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .build();

        ArticleCreateRequest request = new ArticleCreateRequest("title", "content");
        ArticleEntity article = ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();

        when(memberService.findById(any(Long.class))).thenReturn(member);
        when(articleRepository.save(any(ArticleEntity.class))).thenReturn(article);

        // When
        ArticleEntity savedArticle = articleService.save(request, memberId);

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
        Page<ArticleEntity> mockPage = new PageImpl<>(Collections.emptyList());

        when(articleRepository.findAllByIsDeletedFalse(pageable)).thenReturn(mockPage);

        // When
        Page<ArticleEntity> result = articleService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Serivce - findById - 성공")
    void findById_ArticleExists() {
        // Given
        MemberEntity member = new MemberEntity("test@example.com", "password", "testUser");
        ArticleEntity article = new ArticleEntity("title", "content", member);
        when(articleRepository.findById(anyLong())).thenReturn(Optional.of(article));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        // When
        ArticleEntity foundArticle = articleService.findById(1L);

        // Then
        assertNotNull(foundArticle);
        assertEquals("title", foundArticle.getTitle());
        assertEquals("content", foundArticle.getContent());
        assertEquals("test@example.com", foundArticle.getMember().getEmail());
    }

    @Test
    @DisplayName("Serivce - 없는 정보 조회 시 에러 발생")
    void findById_ArticleNotFound() {
        // Given
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MyEntityNotFoundException.class, () -> articleService.findById(1L));
    }

    @Test
    @DisplayName("Service - delete 성공")
    void deleteArticle_Success() {
        // Given
        Long memberId = 3L;
        Long articleId = 1L;
        String email = "test@example.com";
        MemberEntity member = new MemberEntity(email, "testUser", "nickName");
        ArticleEntity article = new ArticleEntity("title", "content", member);

        when(memberService.findById(any(Long.class))).thenReturn(member);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When
        articleService.delete(articleId, memberId);

        // Then
        assertTrue(article.isDeleted()); // softDelete() 호출 후 상태 확인
    }


    @Test
    @DisplayName("Serivce - 다른 사람 게시글 삭제 시 에러 발생")
    void deleteArticle_NotAuthor_ThrowsException() {
        // Given
        Long memberId = 3L; // 요청한 사용자 ID
        long articleId = 1L; // 삭제하려는 게시글 ID
        MemberEntity requestingMember = new MemberEntity("user@example.com", "1234", "requestingUser");
        MemberEntity articleOwner = new MemberEntity("owner@example.com", "1234", "articleOwner");
        ArticleEntity article = new ArticleEntity("title", "content", articleOwner);

        // Mock 설정
        when(memberService.findById(memberId)).thenReturn(requestingMember);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When & Then
        DifferentOwnerException exception = assertThrows(DifferentOwnerException.class,
                () -> articleService.delete(articleId, memberId));

        assertEquals("권한 없음", exception.getMessage());
    }
}
