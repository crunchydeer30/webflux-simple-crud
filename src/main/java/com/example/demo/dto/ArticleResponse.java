package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.Article;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserResponse author,
        List<TagResponse> tags) {
    public static ArticleResponse fromEntity(Article article, UserResponse author, List<TagResponse> tags) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                author,
                tags);
    }
}