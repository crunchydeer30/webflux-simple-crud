package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ArticleResponse;
import com.example.demo.dto.CreateArticleRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Article;
import com.example.demo.model.Tag;
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
                                .flatMap(article -> Mono.zip(
                                                userRepository.findById(article.getAuthorId())
                                                                .map(UserResponse::fromEntity),
                                                tagRepository.findByArticleId(article.getId())
                                                                .map(TagResponse::fromEntity)
                                                                .collectList())
                                                .map(tuple -> ArticleResponse.fromEntity(
                                                                article,
                                                                tuple.getT1(),
                                                                tuple.getT2())));
        }

        public Mono<ArticleResponse> getArticleById(Long id) {
                return articleRepository.findById(id)
                                .flatMap(article -> Mono.zip(
                                                userRepository.findById(article.getAuthorId())
                                                                .map(UserResponse::fromEntity),
                                                tagRepository.findByArticleId(article.getId())
                                                                .map(TagResponse::fromEntity)
                                                                .collectList())
                                                .map(tuple -> ArticleResponse.fromEntity(
                                                                article,
                                                                tuple.getT1(),
                                                                tuple.getT2())))
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Article not found")));
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
                                .flatMap(article -> Mono.zip(
                                                userRepository.findById(article.getAuthorId())
                                                                .map(UserResponse::fromEntity),
                                                tagRepository.findByArticleId(article.getId())
                                                                .map(TagResponse::fromEntity)
                                                                .collectList())
                                                .map(tuple -> ArticleResponse.fromEntity(
                                                                article,
                                                                tuple.getT1(),
                                                                tuple.getT2())));
        }

        private Mono<Void> attachTagsToArticle(Long articleId, List<Long> tagIds) {
                return Flux.fromIterable(null == tagIds ? List.of() : tagIds)
                                .flatMap(tagId -> tagRepository.findById(tagId)
                                                .flatMap(tag -> articleRepository.attachTagToArticle(articleId,
                                                                tag.getId())))
                                .then();
        }
}
