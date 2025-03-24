package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateArticleDto {
  private String title;
  private String content;
  private Long authorId;
}
