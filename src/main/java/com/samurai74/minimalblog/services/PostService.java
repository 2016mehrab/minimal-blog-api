package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post>getDraftPosts(User author);
    Post createPost(User user, CreatePostRequest createPostRequest);
    Post updatePost(UUID postId, UpdatePostRequest updatePostRequest);
    Post getPost(UUID postId);
    void deletePost(UUID postId);
}
