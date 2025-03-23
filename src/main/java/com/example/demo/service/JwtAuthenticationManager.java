package com.example.demo.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.config.JwtUtil;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> auth.getCredentials().toString())
                .flatMap(token -> {
                    if (!jwtUtil.validateToken(token)) {
                        return Mono.error(new BadCredentialsException("Invalid token"));
                    }
                    return jwtUtil.getAuthentication(token);
                });
    }
}