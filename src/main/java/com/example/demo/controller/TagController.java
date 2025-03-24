package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.dto.CreateTagRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.dto.UpdateTagRequest;
import com.example.demo.service.TagService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  // Public endpoints

  @GetMapping
  public Flux<TagResponse> getAllTags() {
    return tagService.getAllTags();
  }

  @GetMapping("/{id}")
  public Mono<TagResponse> getTagById(@PathVariable Long id) {
    return tagService.getTagById(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
    return tagService.createTag(request);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody UpdateTagRequest request) {
    return tagService.updateTag(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteTag(@PathVariable Long id) {
    return tagService.deleteTag(id);
  }
}