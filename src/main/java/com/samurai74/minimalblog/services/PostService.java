package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    Page<Post> getPublishedPosts(Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);
    Page<Post> getPosts(Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    Page<Post> getPendingPosts(Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);

    @PreAuthorize("isAuthenticated()")
    Page<Post> getPendingPostsByUser(UUID userId,Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);

    @PreAuthorize("isAuthenticated()")
    Page<Post> getDraftedPosts(UUID userId,Optional<UUID> categoryId, Optional<UUID> tagId , Pageable pageable);

    Post createPost(User user, CreatePostRequest createPostRequest);
    Post updatePost(UUID userId,UUID postId, UpdatePostRequest updatePostRequest);
    Post getPost(UUID postId);
    void deletePost(UUID userId,UUID postId);
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    void approvePost(UUID postId);
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    void rejectPost(UUID postId);
}
