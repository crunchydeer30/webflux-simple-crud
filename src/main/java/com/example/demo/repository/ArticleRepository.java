package com.example.demo.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demo.model.Article;

import reactor.core.publisher.Mono;

public interface ArticleRepository extends ReactiveCrudRepository<Article, Long> {
  @Query("INSERT INTO articles_tags (article_id, tag_id) VALUES (:articleId, :tagId)")
  Mono<Void> attachTagToArticle(Long articleId, Long tagId);
}
