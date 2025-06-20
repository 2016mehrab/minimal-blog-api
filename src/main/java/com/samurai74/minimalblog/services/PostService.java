package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    Page<Post> getPosts(Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);
    Page<Post> getPendingPosts(Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);
    List<Post>getDraftPosts(User author);
    Post createPost(User user, CreatePostRequest createPostRequest);
    Post updatePost(UUID userId,UUID postId, UpdatePostRequest updatePostRequest);
    Post getPost(UUID postId);
    void deletePost(UUID userId,UUID postId);
    void approvePost(UUID postId);
    void rejectPost(UUID postId);
}
