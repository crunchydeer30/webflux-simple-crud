package com.example.demo.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthResponse;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  public Mono<AuthResponse> login(String email, String password) {
    return userRepository.findByEmail(email)
        .flatMap(user -> {
          if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return Mono.just(new AuthResponse(jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole())));
          }
          return Mono.error(new BadCredentialsException("Invalid password"));
        })
        .switchIfEmpty(Mono.error(new BadCredentialsException("User not found")));
  }
}
