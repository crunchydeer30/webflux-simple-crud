package com.example.demo.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ArticleResponse;
import com.example.demo.dto.TagResponse;
import com.example.demo.dto.UserResponse;
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
}
