package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.User;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static UserResponse fromEntity(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}