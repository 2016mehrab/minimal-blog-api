package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.Role;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.PostRepository;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.services.CategoryService;
import com.samurai74.minimalblog.services.PostService;
import com.samurai74.minimalblog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    @Value("${email.service.from.email}")
    public String EMAIL_SENDER ;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<Post> getPosts(Optional<UUID> categoryId, Optional<UUID> tagId, Pageable pageable) {
        // both present
        if(categoryId.isPresent()  && tagId.isPresent()) {
            return postRepository.findByCategoryIdAndTags_id(categoryId.get(),tagId.get(), pageable);
        }
        // no tags
        else if(categoryId.isPresent()) {
        return postRepository.findByCategoryId(categoryId.get(), pageable);
        }
        // only tags present
        else if(tagId.isPresent()) {
            return postRepository.findByTags_id(tagId.get(), pageable);

        }
        return  postRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPendingPosts(Optional<UUID> categoryId, Optional<UUID> tagId, Pageable pageable) {
        // both present
        if(categoryId.isPresent()  && tagId.isPresent()) {
            return postRepository.findByCategoryIdAndTags_idAndStatus(categoryId.get(),tagId.get(), PostStatus.PENDING,pageable );
        }
        // no tags
        else if(categoryId.isPresent()) {
            return postRepository.findByCategoryIdAndStatus(categoryId.get(), PostStatus.PENDING,pageable);
        }
        // only tags present
        else if(tagId.isPresent()) {
            return postRepository.findByTags_idAndStatus(tagId.get(), PostStatus.PENDING,pageable);

        }
        return  postRepository.findByStatus(PostStatus.PENDING,pageable);
    }

    @Override
   @Transactional(readOnly = true)
    public Page<Post> getPendingPostsByUser(UUID userId, Optional<UUID> categoryId, Optional<UUID> tagId, Pageable pageable) {
        // both present
        if(categoryId.isPresent()  && tagId.isPresent()) {
            return postRepository.findByCategoryIdAndTags_idAndStatusAndAuthorId(categoryId.get(),tagId.get(),PostStatus.PENDING,userId,pageable);
        }
        // no tags
        else if(categoryId.isPresent()) {
            return postRepository.findByCategoryIdAndAuthorIdAndStatus(categoryId.get(),userId,PostStatus.PENDING,pageable);
        }
        // only tags present
        else if(tagId.isPresent()) {
            return postRepository.findByTags_idAndStatusAndAuthorId(tagId.get(),PostStatus.PENDING,userId,pageable);
        }
        return  postRepository.findByAuthorIdAndStatus(userId,PostStatus.PENDING,pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getDraftPosts(User author) {
        return postRepository.findAllByAuthorAndStatus(author, PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        PostStatus status = createPostRequest.getStatus();
        if (status != PostStatus.DRAFT && status != PostStatus.PENDING) {
            throw new AccessDeniedException("Posts can only be created as DRAFT or PENDING.");
        }
        var newPost = Post.builder().author(user)
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                // could be draft/ published
                // published should not be allowed
                // pending allowed
                .status(createPostRequest.getStatus())
                .category(categoryService.getCategoryById(createPostRequest.getCategoryId()))
                .readingTime(calculateReadingTime(createPostRequest.getContent()))
                .tags(tagService.getTagByIds(createPostRequest.getTagIds()))
                .build();
        return postRepository.save(newPost);
    }

    @Override
    @Transactional
    public Post updatePost(UUID userId,UUID postId, UpdatePostRequest updatePostRequest) {
        var existingPost = postRepository.findById(postId). orElseThrow(()->new EntityNotFoundException("Post not found"));

        var postAuthorId = existingPost.getAuthor().getId();
        var user= userRepository.findById(userId);
        // normal user tried to edit other users post
        if(!postAuthorId.equals(userId) &&  user.isPresent() && user.get().getRole().equals(Role.USER)) {
            log.info("authorRole {}", user.get().getRole());
            throw new AccessDeniedException("You are not allowed to edit this post");
        }

        existingPost.setTitle(updatePostRequest.getTitle());
        existingPost.setContent(updatePostRequest.getContent());
        existingPost.setStatus(updatePostRequest.getStatus());
        existingPost.setReadingTime(calculateReadingTime(updatePostRequest.getContent()));

        if (!existingPost.getCategory().getId().equals(updatePostRequest.getCategoryId())) {
           var cat= categoryService.getCategoryById(updatePostRequest.getCategoryId()) ;
           existingPost.setCategory(cat);
        }
        var existingTagIds = existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        var updatedTagIds = updatePostRequest.getTagIds();
        if (!existingTagIds.equals(updatedTagIds)){
            var updatedTags = tagService.getTagByIds(updatedTagIds);
            existingPost.setTags(updatedTags);
        }
        // do nothing if category didn't change
        return postRepository.save(existingPost);
    }

    @Transactional(readOnly = true)
    @Override
    public Post getPost(UUID postId) {
        return postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("Post not found"));
    }

    @Override
    @Transactional
    public void deletePost(UUID userId,UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
        // post exists
        var postAuthorId = post.getAuthor().getId();
        if(!postAuthorId.equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this post");
        }
        post.getTags().forEach(tag-> tag.getPosts().remove(post));
        post.getTags().clear();
        postRepository.saveAndFlush(post);
        postRepository.deleteById(postId);
    }

    @Override
    @Transactional
    public void approvePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
        if(!post.getStatus().equals(PostStatus.PENDING)) {
            throw new IllegalArgumentException("Post status must be PENDING");
        }
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);
        // implement email send logic
        StringBuilder sb = new StringBuilder();
        sb.append("Your post '");
        sb.append(post.getTitle());
        sb.append("' has been approved and published successfully.");
        sb.append("\n");
        SimpleMailMessage mailMessage= new SimpleMailMessage();
        mailMessage.setTo(post.getAuthor().getEmail());
        mailMessage.setSubject("Post Approval from minimal-blog");
        mailMessage.setFrom(EMAIL_SENDER);
        mailMessage.setText(sb.toString());
        javaMailSender.send(mailMessage);
    }

    @Override
    @Transactional
    public void rejectPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
        if(!post.getStatus().equals(PostStatus.PENDING)) {
            throw new IllegalArgumentException("Post status must be PENDING");
        }
        post.setStatus(PostStatus.DRAFT);
        postRepository.save(post);
        // implement email send logic
        StringBuilder sb = new StringBuilder();
        sb.append("Your post ");
        sb.append(post.getTitle());
        sb.append(" has been rejected and has been moved to drafts.");
        sb.append("\n");
        SimpleMailMessage mailMessage= new SimpleMailMessage();
        mailMessage.setTo(post.getAuthor().getEmail());
        mailMessage.setSubject("Post Rejection");
        mailMessage.setFrom(EMAIL_SENDER);
        mailMessage.setText(sb.toString());
        javaMailSender.send(mailMessage);
    }

    private Integer calculateReadingTime(String content) {
        if(content==null || content.isEmpty()) return 0;
        int wordCount= content.trim().split("\\s+").length;
        return (int) Math.ceil((wordCount *1.0/ Constants.WORDS_PER_MINUTE));
    }
}
