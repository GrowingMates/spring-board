package com.board.board.service;

import com.board.board.dto.request.ArticleCreateRequest;
import com.board.board.dto.request.ArticleUpdateRequest;
import com.board.board.entity.ArticleEntity;
import com.board.board.repository.BlogRepository;
import com.board.config.auth.AuthUtil;
import com.board.exception.custom.MyEntityNotFoundException;
import com.board.member.entity.MemberEntity;
import com.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final MemberService memberService;
    private final AuthUtil authUtil;

    public ArticleEntity save(ArticleCreateRequest request) {
        String email = authUtil.getMemberEmail();
        MemberEntity member = memberService.findByEmail(email);

        return blogRepository.save(ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<ArticleEntity> findAll(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ArticleEntity findById(long id) {
        return findArticle(id);
    }

    public void delete(long id) {
        compareAuthors(id);
        blogRepository.deleteById(id);
    }

    public ArticleEntity update(long id, ArticleUpdateRequest request) {
        ArticleEntity article = compareAuthors(id);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private ArticleEntity compareAuthors(long articleId) {
        String email = authUtil.getMemberEmail();
        MemberEntity member = memberService.findByEmail(email);
        ArticleEntity article = findArticle(articleId);
        article.validateOwner(member);
        return article;
    }

    private ArticleEntity findArticle(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }
}
