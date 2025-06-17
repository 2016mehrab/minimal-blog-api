package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
    List<Tag> getAllTags();
    List<Tag> createTags(Set<String> tags);
    void deleteTag(UUID tagId);
    Tag getTagById(UUID tagId);
    Tag editTagById(UUID tagId, String name);
    Set<Tag>getTagByIds(Set<UUID> tagIds);
}
