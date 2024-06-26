package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.dto.request.ArticleCreateRequest;
import com.example.demo.controller.dto.response.ArticleResponse;
import com.example.demo.controller.dto.request.ArticleUpdateRequest;
import com.example.demo.domain.Article;
import com.example.demo.domain.Board;
import com.example.demo.domain.Member;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public ArticleService(
            ArticleRepository articleRepository,
            MemberRepository memberRepository,
            BoardRepository boardRepository
    ) {
        this.articleRepository = articleRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
    }

    public ArticleResponse getById(Long id) {
        Article article = articleRepository.findById(id);
        Member member = memberRepository.findById(article.getAuthor_id());
        Board board = boardRepository.findById(article.getBoard_id());
        return ArticleResponse.of(article, member, board);
    }

    public List<ArticleResponse> getByBoardId(Long boardId) {
        List<Article> articles = articleRepository.findAllByBoardId(boardId);
        return articles.stream()
                .map(article -> {
                    Member member = memberRepository.findById(article.getAuthor_id());
                    Board board = boardRepository.findById(article.getBoard_id());
                    return ArticleResponse.of(article, member, board);
                })
                .toList();
    }

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = new Article(
                request.author_id(),
                request.board_id(),
                request.title(),
                request.content()
        );
        Article saved = articleRepository.insert(article);
        Member member = memberRepository.findById(saved.getAuthor_id());
        Board board = boardRepository.findById(saved.getBoard_id());
        return ArticleResponse.of(saved, member, board);
    }

    @Transactional
    public ArticleResponse update(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id);
        article.update(request.boardId(), request.title(), request.description());
        Article updated = articleRepository.update(article);
        Member member = memberRepository.findById(updated.getAuthor_id());
        Board board = boardRepository.findById(article.getBoard_id());
        return ArticleResponse.of(article, member, board);
    }

    @Transactional
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
