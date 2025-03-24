package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.ArticleResponse;
import com.example.demo.dto.CreateArticleRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.dto.UpdateArticleRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ArticleService {
        private final ArticleRepository articleRepository;
        private final UserRepository userRepository;
        private final TagRepository tagRepository;

        public Flux<ArticleResponse> getAllArticles() {
                return articleRepository.findAll()
                                .flatMap(this::fetchRelationships);
        }

        public Mono<ArticleResponse> getArticleById(Long id) {
                return articleRepository.findById(id)
                                .flatMap(this::fetchRelationships)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Article not found")));
        }

        public Mono<ArticleResponse> create(CreateArticleRequest dto, Long userId) {
                return Mono.just(dto)
                                .map(data -> Article.builder()
                                                .title(data.getTitle())
                                                .content(data.getContent())
                                                .authorId(userId)
                                                .build())
                                .flatMap(articleRepository::save)
                                .flatMap(savedArticle -> attachTagsToArticle(savedArticle.getId(), dto.getTagIds())
                                                .thenReturn(savedArticle))
                                .flatMap(this::fetchRelationships);
        }

        public Mono<ArticleResponse> updateArticle(Long articleId, UpdateArticleRequest request, Long userId) {
                return articleRepository.findById(articleId)
                                .switchIfEmpty(Mono.error(
                                                new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found")))
                                .flatMap(article -> {
                                        if (!article.getAuthorId().equals(userId)) {
                                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                                "Not an author of an article"));
                                        }
                                        if (request.getTitle() != null) {
                                                article.setTitle(request.getTitle());
                                        }
                                        if (request.getContent() != null) {
                                                article.setContent(request.getContent());
                                        }

                                        article.setUpdatedAt(LocalDateTime.now());
                                        return articleRepository.save(article);
                                })
                                .flatMap(this::fetchRelationships);
        }

        private Mono<Void> attachTagsToArticle(Long articleId, List<Long> tagIds) {
                return Flux.fromIterable(null == tagIds ? List.of() : tagIds)
                                .flatMap(tagId -> tagRepository.findById(tagId)
                                                .flatMap(tag -> articleRepository.attachTagToArticle(articleId,
                                                                tag.getId())))
                                .then();
        }

        private Mono<ArticleResponse> fetchRelationships(Article article) {
                return Mono.zip(
                                userRepository.findById(article.getAuthorId())
                                                .map(UserResponse::fromEntity),
                                tagRepository.findByArticleId(article.getId())
                                                .map(TagResponse::fromEntity)
                                                .collectList())
                                .map(tuple -> ArticleResponse.fromEntity(
                                                article,
                                                tuple.getT1(),
                                                tuple.getT2()));
        }
}
