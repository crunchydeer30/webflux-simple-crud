package com.example.demo.config;

import java.security.Key;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtUtil {
  // TODO: ЕНВЫ!!
  private final String SECRET_KEY = "your-256-bit-secret-key-here-must-be-at-least-32-chars";
  private final long EXPIRATION_TIME = 86400000;

  private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

  public String generateToken(Long userId, String username, String role) {
    return Jwts.builder()
        .claim("userId", userId)
        .claim("role", role)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key)
        .compact();
  }

  public Mono<Long> extractUserId(String token) {
    return Mono.fromCallable(() -> {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
      return claims.get("userId", Long.class);
    });
  }

  public Mono<Authentication> getAuthentication(String token) {
    return Mono.fromCallable(() -> {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      Long userId = claims.get("userId", Long.class);
      String username = claims.getSubject();
      String role = claims.get("role", String.class);

      return new JwtAuthenticationToken(userId, username, role);
    });
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}