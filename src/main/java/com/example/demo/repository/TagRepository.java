package com.example.demo.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demo.model.Tag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagRepository extends ReactiveCrudRepository<Tag, Long> {
  @Query("SELECT t.* FROM tags t INNER JOIN articles_tags at ON t.id = at.tag_id WHERE at.article_id = :articleId")
  Flux<Tag> findByArticleId(Long articleId);

  Mono<Tag> findByName(String name);
}
