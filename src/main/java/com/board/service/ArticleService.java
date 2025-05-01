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
        return articleRepository.findAll(pageable);
    }

    @Transactional
    public ArticleEntity findByIdAndIncreaseViewCount(long id) {
        ArticleEntity article = findById(id);
        article.increaseViewCount();
        return article;
    }

    @Transactional
    public void delete(long id, Long memberId) {
        compareAuthors(id, findMemberById(memberId));
        articleRepository.deleteById(id);
    }

    @Transactional
    public ArticleEntity update(long id, Long memberId, ArticleUpdateRequest request) {
        ArticleEntity article = findById(id);
        compareAuthors(id, findMemberById(memberId));
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private void compareAuthors(long articleId, MemberEntity member) {
        ArticleEntity article = findById(articleId);
        article.validateOwner(member);
    }

    public ArticleEntity findById(long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    private MemberEntity findMemberById(Long memberId) {
        return memberService.findById(memberId);
    }
}
