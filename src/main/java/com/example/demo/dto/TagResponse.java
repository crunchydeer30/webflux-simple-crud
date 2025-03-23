package com.example.demo.dto;

import com.example.demo.model.Tag;

public record TagResponse(
    Long id,
    String name) {
  public static TagResponse fromEntity(Tag tag) {
    return new TagResponse(
        tag.getId(),
        tag.getName());
  }
}
