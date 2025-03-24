package com.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtUtil {

  private final String secret;
  private final long expirationTime;
  private final Key key;

  public JwtUtil(@Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-time}") long expirationTime) {
    this.secret = secret;
    this.expirationTime = expirationTime;
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Long userId, String username, String role) {
    return Jwts.builder()
        .claim("userId", userId)
        .claim("role", role)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
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
