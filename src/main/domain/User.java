package com.example.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
  @Id
  private Long id;
  private String username;
  private String email;

  @Column("password_hash")
  private String passwordHash;

  private String role;

  @Column("created_at")
  @Builder.Default
  private Instant createdAt = Instant.now();

  @Column("updated_at")
  @Builder.Default
  private Instant updatedAt = Instant.now();
}