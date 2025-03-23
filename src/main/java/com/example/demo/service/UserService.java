package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.DuplicateFormatFlagsException;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  public Mono<AuthResponse> create(CreateUserRequest dto) {
    return Mono.just(dto)
        .flatMap(data -> checkUsernameExists(data.getUsername())
            .thenReturn(data))
        .flatMap(data -> checkEmailExists(data.getEmail())
            .thenReturn(data))
        .map(data -> User.builder()
            .username(data.getUsername())
            .email(data.getEmail())
            .passwordHash(BCrypt.hashpw(data.getPassword(), BCrypt.gensalt()))
            .role(UserRole.USER.name())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build())
        .flatMap(userRepository::save)
        .flatMap(user -> Mono
            .just(new AuthResponse(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole()))));
  }

  private Mono<Void> checkUsernameExists(String username) {
    return userRepository.findByUsername(username)
        .flatMap(existingUser -> Mono.error(new DuplicateFormatFlagsException("Username already exists")))
        .then();
  }

  private Mono<Void> checkEmailExists(String email) {
    return userRepository.findByEmail(email)
        .flatMap(existingUser -> Mono.error(new DuplicateFormatFlagsException("Email already exists")))
        .then();
  }
}