package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.repositories.TagRepository;
import com.samurai74.minimalblog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        var existingTags = tagRepository.findByNameInIgnoreCase(tagNames);
        // get the names
        var existingTagsNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());
        List<Tag> newTags = tagNames.stream()
                // if name doesnt exist
                .filter(name->!existingTagsNames.contains(name))

                .map(name-> Tag.builder().name(name).posts(new HashSet<>()).build()).toList();
        List<Tag> savedTags = new ArrayList<>();
        if(!newTags.isEmpty()) {
            savedTags = tagRepository.saveAll(newTags);
        }
        savedTags.addAll(existingTags);
        return savedTags;
    }

    @Transactional
    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.findById(tagId).ifPresent(tag -> {
            if(!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("Cannot delete tag with posts");
            }
            tagRepository.deleteById(tagId);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Tag getTagById(UUID tagId) {
        return tagRepository.findById(tagId).orElseThrow(()->new EntityNotFoundException("No tag with id " + tagId));
    }

    @Override
    public Set<Tag> getTagByIds(Set<UUID> tagIds) {
        var foundTags=tagRepository.findAllById(tagIds);
        if(foundTags.size() != tagIds.size()) {
            throw new EntityNotFoundException("No tags with id " + tagIds);
        }
        return new HashSet<>(foundTags);
    }


}
