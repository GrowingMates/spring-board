package com.board.service;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.repository.ArticleRepository;
import com.exception.custom.MyEntityNotFoundException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberService memberService;

    @Transactional
    public ArticleEntity save(ArticleCreateRequest request, Long memberId) {
        return articleRepository.save(ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(findMemberById(memberId))
                .build());
    }

    public Page<ArticleEntity> findAll(Pageable pageable) {
        return articleRepository.findAllByIsDeletedFalse(pageable);
    }

    @Transactional
    public ArticleEntity findByIdAndIncreaseViewCount(Long id) {
        ArticleEntity article = findById(id);
        article.increaseViewCount();
        return article;
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        getOwnedArticle(id, memberId).softDelete();
    }

    @Transactional
    public ArticleEntity update(Long id, Long memberId, ArticleUpdateRequest request) {
        ArticleEntity article = getOwnedArticle(id, memberId);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private ArticleEntity getOwnedArticle(Long articleId, Long memberId) {
        ArticleEntity article = findById(articleId);
        MemberEntity member = findMemberById(memberId);
        article.validateOwner(member);
        return article;
    }

    public ArticleEntity findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    private MemberEntity findMemberById(Long memberId) {
        return memberService.findById(memberId);
    }
}
