package com.board.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.board.board.dto.request.ArticleCreateRequest;
import com.board.board.entity.ArticleEntity;
import com.board.board.repository.BlogRepository;
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
        when(blogRepository.save(any(ArticleEntity.class))).thenReturn(article);

        // When
        ArticleEntity savedArticle = blogService.save(request, memberId);

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

        when(blogRepository.findAll(pageable)).thenReturn(mockPage);

        // When
        Page<ArticleEntity> result = blogService.findAll(pageable);

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
        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(article));
        when(blogRepository.findById(1L)).thenReturn(Optional.of(article));
        // When
        ArticleEntity foundArticle = blogService.findById(1L);

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
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MyEntityNotFoundException.class, () -> blogService.findById(1L));
    }

    @Test
    @DisplayName("Service - delete 성공")
    void deleteArticle_Success() {
        // Given
        Long memberId = 3L;
        Long articleId = 1L;
        String email = "test@example.com";
        MemberEntity member = new MemberEntity(email, "testUser", "nickName"); // ID 없이 생성
        ArticleEntity article = new ArticleEntity("title", "content", member); // ID 없이 생성

        when(memberService.findById(any(Long.class))).thenReturn(member);
        when(blogRepository.findById(articleId)).thenReturn(Optional.of(article));

        doNothing().when(blogRepository).deleteById(articleId);

        // When
        blogService.delete(articleId, memberId);

        // Then
        verify(blogRepository, times(1)).deleteById(anyLong());
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
        when(blogRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When & Then
        DifferentOwnerException exception = assertThrows(DifferentOwnerException.class,
                () -> blogService.delete(articleId, memberId));

        assertEquals("권한 없음", exception.getMessage());
    }
}
