package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.dtos.CreatePostRequestDto;
import com.samurai74.minimalblog.domain.dtos.PostDto;
import com.samurai74.minimalblog.domain.dtos.UpdatePostRequestDto;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.mappers.PostMapper;
import com.samurai74.minimalblog.services.PostService;
import com.samurai74.minimalblog.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
@Tag(name="Post")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required=false)UUID categoryId,
            @RequestParam(required=false)UUID tagId
            ) {
       var posts =  postService.getAllPosts(categoryId, tagId);
       List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
       return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<PostDto>> getAllDrafts(
            @RequestAttribute UUID userId
    ) {
        User loggedInUser = userService.getUserById(userId);
        var posts = postService.getDraftPosts(loggedInUser);
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return ResponseEntity.ok(postDtos);
    }
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody CreatePostRequestDto requestDto ,
                                             @RequestAttribute UUID userId ) {
        User loggedInUser = userService.getUserById(userId);
        var createPostRequest= postMapper.toCreatePostRequest(requestDto);
        var savedPost = postService.createPost(loggedInUser, createPostRequest);
        return new ResponseEntity<>(postMapper.toPostDto(savedPost), HttpStatus.CREATED);
    }
    @PutMapping(path = "/{id}")
    public  ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto
            ){

       UpdatePostRequest updatePostRequest =  postMapper.toUpdatePostRequest(updatePostRequestDto);
       Post updatedPost = postService.updatePost(id, updatePostRequest);
      return ResponseEntity.ok(postMapper.toPostDto(updatedPost));
    }

    @GetMapping(path = "/{id}")
    public  ResponseEntity<PostDto> getPost(
            @PathVariable UUID id,
            @RequestAttribute UUID userId
    ){
        Post post= postService.getPost(id);
        if(post.getStatus() == PostStatus.DRAFT && userId == null){
            throw new AccessDeniedException("You are not authorized.");
        }
        return ResponseEntity.ok(postMapper.toPostDto(post));
    }

    @DeleteMapping(path = "/{id}")
    public  ResponseEntity<Void> deletePost(
            @PathVariable UUID id
    ){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
