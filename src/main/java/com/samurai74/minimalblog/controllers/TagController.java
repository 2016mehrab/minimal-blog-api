package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.dtos.*;
import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.mappers.TagMapper;
import com.samurai74.minimalblog.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/tags")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name="Tag")
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tagMapper.toTagResponseList(tags));
    }

    @PostMapping
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<List<TagDto>> createTags(@Valid @RequestBody CreateTagRequest request) {
        log.info("requested new tags: {}", request.getTags());
        var allTags = tagService.createTags(request.getTags());
        return ResponseEntity.ok(tagMapper.toTagResponseList(allTags));
    }

    @PutMapping(path = "/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<TagDto> updateTag(@PathVariable UUID id , @Valid @RequestBody UpdateTagName request) {
        var updatedTag = tagService.editTagById(id, request.getName());
        return ResponseEntity.ok(tagMapper.toResponse(updatedTag));
    }

    @DeleteMapping(path = "/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<Void>deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
