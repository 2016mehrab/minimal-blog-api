package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
}
