package com.samurai74.minimalblog.repositories;

import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // post can have many tags so Tags should be correct
    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag);
    List<Post> findAllByStatusAndCategory(PostStatus status, Category category);
    List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);
    List<Post> findAllByStatus(PostStatus status);
    List<Post> findAllByAuthorAndStatus(User author,PostStatus status);
    Page<Post> findByCategoryId(UUID categoryId, Pageable pageable);
    Page<Post> findByCategoryIdAndTags_id(UUID categoryId, UUID tagsId, Pageable pageable);
    Page<Post> findByTags_id(UUID tagsId, Pageable pageable);
    Page<Post> findByCategoryIdAndTags_idAndStatus(UUID categoryId, UUID tagsId, PostStatus status, Pageable pageable);
    Page<Post> findByCategoryIdAndStatus(UUID categoryId, PostStatus status, Pageable pageable);
    Page<Post> findByTags_idAndStatus(UUID tagsId, PostStatus status, Pageable pageable);

    Page<Post> findByCategoryIdAndTags_idAndStatusAndAuthorId(UUID categoryId, UUID tagsId, PostStatus status, UUID authorId, Pageable pageable);
    Page<Post> findByTags_idAndStatusAndAuthorId(UUID tagsId, PostStatus status, UUID authorId, Pageable pageable);
    Page<Post> findByCategoryIdAndAuthorIdAndStatus(UUID categoryId, UUID authorId, PostStatus status, Pageable pageable);
    Page<Post> findByAuthorIdAndStatus(UUID authorId, PostStatus status, Pageable pageable);


    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findAllByCategory_Id(UUID categoryId);
}
