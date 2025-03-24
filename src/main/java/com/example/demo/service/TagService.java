package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CreateTagRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.dto.UpdateTagRequest;
import com.example.demo.model.Tag;
import com.example.demo.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  public Flux<TagResponse> getAllTags() {
    return tagRepository.findAll()
        .map(TagResponse::fromEntity);
  }

  public Mono<TagResponse> getTagById(Long id) {
    return tagRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")))
        .map(TagResponse::fromEntity);
  }

  public Mono<TagResponse> createTag(CreateTagRequest request) {
    Tag tag = Tag.builder()
        .name(request.getName())
        .build();
    return tagRepository.save(tag)
        .map(TagResponse::fromEntity);
  }

  // UPDATE an existing tag (admin only)
  public Mono<TagResponse> updateTag(Long id, UpdateTagRequest request) {
    return tagRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")))
        .flatMap(tag -> {
          tag.setName(request.getName());
          tag.setUpdatedAt(LocalDateTime.now());
          return tagRepository.save(tag);
        })
        .map(TagResponse::fromEntity);
  }

  // DELETE a tag (admin only)
  public Mono<Void> deleteTag(Long id) {
    return tagRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")))
        .flatMap(tag -> tagRepository.deleteById(tag.getId()));
  }
}
