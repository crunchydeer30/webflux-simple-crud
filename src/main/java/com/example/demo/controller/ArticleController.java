package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ArticleResponse;
import com.example.demo.service.ArticleService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
