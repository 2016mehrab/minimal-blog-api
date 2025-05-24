package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.dtos.CreateTagRequest;
import com.samurai74.minimalblog.domain.dtos.TagDto;
import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.mappers.TagMapper;
import com.samurai74.minimalblog.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TagDto>> createTags(@RequestBody CreateTagRequest request) {
        log.info("requested new tags: {}", request.getTags());
        var allTags = tagService.createTags(request.getTags());
        return ResponseEntity.ok(tagMapper.toTagResponseList(allTags));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void>deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
