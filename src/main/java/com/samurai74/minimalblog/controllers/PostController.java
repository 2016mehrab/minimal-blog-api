package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.dtos.PostDto;
import com.samurai74.minimalblog.mappers.PostMapper;
import com.samurai74.minimalblog.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required=false)UUID categoryId,
            @RequestParam(required=false)UUID tagId
            ) {
       var posts =  postService.getAllPosts(categoryId, tagId);
       List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
       return ResponseEntity.ok(postDtos);
    }
}
