package com.example.demo.controller;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtAuthenticationToken;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.dto.CreateArticleRequest;
import com.example.demo.dto.UpdateArticleRequest;
import com.example.demo.service.ArticleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
  private final ArticleService articleService;

  @GetMapping
  public Flux<ArticleResponse> getAllArticles() {
    return articleService.getAllArticles();
  }

  @GetMapping("/{id}")
  public Mono<ArticleResponse> getArticleById(@PathVariable Long id) {
    return articleService.getArticleById(id);
  }

  @PostMapping("")
  public Mono<ArticleResponse> postMethodName(@Valid @RequestBody CreateArticleRequest body) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .cast(JwtAuthenticationToken.class)
        .flatMap(auth -> {
          Long userId = auth.getUserId();
          return articleService.create(body, userId);
        });
  }

  @PatchMapping("/{id}")
  public Mono<ArticleResponse> updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest body) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .cast(JwtAuthenticationToken.class)
        .flatMap(auth -> {
          Long userId = auth.getUserId();
          return articleService.updateArticle(id, body, userId);
        });
  }
}
