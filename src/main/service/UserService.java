package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Mono<User> createUser(User user) {
    user.setCreatedAt(Instant.now());
    user.setUpdatedAt(Instant.now());
    return userRepository.save(user);
  }

  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Mono<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public Mono<User> updateUser(Long id, User userDetails) {
    return userRepository.findById(id)
        .flatMap(existingUser -> {
          existingUser.setUsername(userDetails.getUsername());
          existingUser.setEmail(userDetails.getEmail());
          existingUser.setPasswordHash(userDetails.getPasswordHash());
          existingUser.setRole(userDetails.getRole());
          existingUser.setUpdatedAt(Instant.now());
          return userRepository.save(existingUser);
        });
  }

  public Mono<Void> deleteUser(Long id) {
    return userRepository.deleteById(id);
  }
}