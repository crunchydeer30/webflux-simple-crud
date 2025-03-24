package com.example.demo.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private final Long userId;

  public JwtAuthenticationToken(Long userId, String username, String role) {
    super(username, null, AuthorityUtils.createAuthorityList(role));
    this.userId = userId;
  }

  public Long getUserId() {
    return userId;
  }
}