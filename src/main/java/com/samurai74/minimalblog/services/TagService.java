package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
    List<Tag> getAllTags();

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    List<Tag> createTags(Set<String> tags);

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    void deleteTag(UUID tagId);
    Tag getTagById(UUID tagId);

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    Tag editTagById(UUID tagId, String name);

    Set<Tag>getTagByIds(Set<UUID> tagIds);
}
