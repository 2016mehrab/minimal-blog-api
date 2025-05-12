package com.samurai74.minimalblog.repositories;

import com.samurai74.minimalblog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    @Query("SELECT t from Tag t left join fetch t.posts")
    List<Tag> findAllWithPostCount();
    // basically SELECT * FROM tag WHERE name IN ('java', 'spring', 'docker');
    List<Tag> findByNameInIgnoreCase(Set<String> names);
}
