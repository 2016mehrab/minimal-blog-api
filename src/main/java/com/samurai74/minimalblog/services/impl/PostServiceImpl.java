package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.PostRepository;
import com.samurai74.minimalblog.services.CategoryService;
import com.samurai74.minimalblog.services.PostService;
import com.samurai74.minimalblog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    @Override
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if(categoryId!=null && tagId!=null) {
            var category =categoryService.getCategoryById(categoryId);
            var tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndCategoryAndTagsContaining(PostStatus.PUBLISHED, category, tag);
        }
        if (categoryId!=null) {
            var category =categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category);
        }
        if (tagId!=null) {
            var tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tag);
        }
        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getDraftPosts(User author) {
        return postRepository.findAllByAuthorAndStatus(author, PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        var newPost = Post.builder().author(user)
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .status(createPostRequest.getStatus())
                .category(categoryService.getCategoryById(createPostRequest.getCategoryId()))
                .readingTime(calculateReadingTime(createPostRequest.getContent()))
                .tags(tagService.getTagByIds(createPostRequest.getTagIds()))
                .build();
        return postRepository.save(newPost);
    }

    private Integer calculateReadingTime(String content) {
        if(content==null || content.isEmpty()) return 0;
        int wordCount= content.trim().split("\\s+").length;
        return (int) Math.ceil((wordCount *1.0/ Constants.WORDS_PER_MINUTE));
    }
}
