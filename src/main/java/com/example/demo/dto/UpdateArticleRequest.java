package com.example.demo.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UpdateArticleRequest {
  @Nullable
  String title;

  @Nullable
  String content;
}
