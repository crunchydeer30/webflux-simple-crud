package com.example.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demo.model.Article;

public interface ArticleRepository extends ReactiveCrudRepository<Article, Long> {
}
