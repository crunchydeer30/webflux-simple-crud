package com.example.demo.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateArticleRequest {
  @NotBlank
  private String title;

  @NotBlank
  private String content;

  private List<Long> tagIds;
}
