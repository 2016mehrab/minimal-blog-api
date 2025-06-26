package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.constant.Constants;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<Page<PostDto>> getPublishedPosts(
            @RequestParam(required = false)UUID categoryId,
            @RequestParam(required = false)UUID tagId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = Constants.PAGE_SIZE+"")int size,
            @RequestParam(defaultValue = "createdAt, desc")String[] sort
            ) {
        Sort sortOrder =Sort.by(sort[0]);
        if(sort.length == 2 && sort[1].equalsIgnoreCase("desc")){
            sortOrder= sortOrder.descending();
        }else sortOrder=sortOrder.ascending();
        Pageable pageable = PageRequest.of(page,size,sortOrder) ;

       var postPage =  postService.getPublishedPosts(Optional.ofNullable(categoryId) ,Optional.ofNullable(tagId),pageable);
        Page<PostDto> postDtoPage =postPage.map(postMapper::toPostDto);
       return ResponseEntity.ok(postDtoPage);
    }

    @GetMapping(path = "/blogs")
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(required = false)UUID categoryId,
            @RequestParam(required = false)UUID tagId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = Constants.PAGE_SIZE+"")int size,
            @RequestParam(defaultValue = "createdAt, desc")String[] sort
    ){
        Sort sortOrder =Sort.by(sort[0]);
        if(sort.length == 2 && sort[1].equalsIgnoreCase("desc")){
           sortOrder= sortOrder.descending();
        }else sortOrder=sortOrder.ascending();
       Pageable pageable = PageRequest.of(page,size,sortOrder) ;
       Page<Post> postPage=postService.getPosts(Optional.ofNullable(categoryId) ,Optional.ofNullable(tagId),pageable);
       Page<PostDto> postDtoPage =postPage.map(postMapper::toPostDto);
       return ResponseEntity.ok(postDtoPage);
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
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostRequestDto requestDto ,
                                             @RequestAttribute UUID userId ) {
        User loggedInUser = userService.getUserById(userId);
        var createPostRequest= postMapper.toCreatePostRequest(requestDto);
        var savedPost = postService.createPost(loggedInUser, createPostRequest);
        return new ResponseEntity<>(postMapper.toPostDto(savedPost), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public  ResponseEntity<PostDto> updatePost(
            @RequestAttribute UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto
            ){

       UpdatePostRequest updatePostRequest =  postMapper.toUpdatePostRequest(updatePostRequestDto);
       Post updatedPost = postService.updatePost(userId,id, updatePostRequest);
      return ResponseEntity.ok(postMapper.toPostDto(updatedPost));
    }

    @GetMapping(path = "/pending")
    public ResponseEntity<Page<PostDto>> getPendingPosts(
            @RequestParam(required = false)UUID categoryId,
            @RequestParam(required = false)UUID tagId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = Constants.PAGE_SIZE+"")int size,
            @RequestParam(defaultValue = "createdAt, desc")String[] sort
    ){
        Sort sortOrder =Sort.by(sort[0]);
        if(sort.length == 2 && sort[1].equalsIgnoreCase("desc")){
            sortOrder= sortOrder.descending();
        }else sortOrder=sortOrder.ascending();
        Pageable pageable = PageRequest.of(page,size,sortOrder) ;
        Page<Post> postPage=postService.getPendingPosts(Optional.ofNullable(categoryId) ,Optional.ofNullable(tagId),pageable);
        Page<PostDto> postDtoPage =postPage.map(postMapper::toPostDto);
        return ResponseEntity.ok(postDtoPage);
    }

    @GetMapping(path = "/user-pending")
    public ResponseEntity<Page<PostDto>> getPendingPosts(
            @RequestAttribute UUID userId,
            @RequestParam(required = false)UUID categoryId,
            @RequestParam(required = false)UUID tagId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = Constants.PAGE_SIZE+"")int size,
            @RequestParam(defaultValue = "createdAt, desc")String[] sort
    ){
        Sort sortOrder =Sort.by(sort[0]);
        if(sort.length == 2 && sort[1].equalsIgnoreCase("desc")){
            sortOrder= sortOrder.descending();
        }else sortOrder=sortOrder.ascending();

        Pageable pageable = PageRequest.of(page,size,sortOrder) ;
        Page<Post> postPage=postService.getPendingPostsByUser(userId,Optional.ofNullable(categoryId) ,Optional.ofNullable(tagId),pageable);
        Page<PostDto> postDtoPage =postPage.map(postMapper::toPostDto);
        return ResponseEntity.ok(postDtoPage);
    }

    @PutMapping(path = "/{id}/approve")
    public  ResponseEntity<PostDto> approvePost(
            @PathVariable UUID id
    ){
        postService.approvePost(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping(path = "/{id}/reject")
    public  ResponseEntity<PostDto> rejectPost(
            @PathVariable UUID id
    ){
         postService.rejectPost(id);
        return ResponseEntity.ok().build();
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
            @RequestAttribute UUID userId ,
            @PathVariable UUID id
    ){
        postService.deletePost(userId,id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/ping")
    public  ResponseEntity<Void> ping(@CookieValue (name = "ping" , defaultValue = "0", required = false) String ping
    ){
        var pingcnt = Integer.parseInt(ping);
        var pingcookie= ResponseCookie.from("ping", String.valueOf(pingcnt+1))
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(3600)
                .secure(false)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,pingcookie.toString()).build();
    }

}
