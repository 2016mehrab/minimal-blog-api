package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.PostStatus;
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
                .filter(name->!existingTagsNames.contains(name.toLowerCase()))
                .map(name-> Tag.builder().name(name.toLowerCase()).posts(new HashSet<>()).build()).toList();
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
        // fetch all post
        // delete if no published posts
        var tag = tagRepository.findById(tagId).orElseThrow(()->new EntityNotFoundException("Tag does not exist"));
        tag.getPosts().stream().forEach(post -> {
            if(!post.getStatus().equals(PostStatus.DRAFT)) throw new IllegalStateException("Cannot delete tag '" + tag.getName()+ "' as it is associated with published or pending posts. " +
                    "Please unpublish/reject these posts or remove the tag from them before deleting the tag.");
        });
        // all remaining posts are drafts so tag can be deleted
        tagRepository.deleteById(tagId);
    }

    @Transactional(readOnly = true)
    @Override
    public Tag getTagById(UUID tagId) {
        return tagRepository.findById(tagId).orElseThrow(()->new EntityNotFoundException("No tag with id " + tagId));
    }

    @Transactional
    @Override
    public Tag editTagById(UUID tagId, String name) {
        Tag existingTag = tagRepository.findById(tagId).orElseThrow(()->new EntityNotFoundException("No tag with id " + tagId));

        var existingTags = tagRepository.findByNameIgnoreCase(name);
        // edited with the previous name
        if(!existingTags.isEmpty() && existingTags.getFirst().getId().equals(existingTag.getId())) {
            return existingTags.getFirst();
        }
        // same name as another existing tag
        else if(!existingTags.isEmpty() && !existingTags.getFirst().getId().equals(existingTag.getId())){
            throw new IllegalArgumentException("Cannot edit tag with id " + existingTag.getId());
        }

        existingTag.setName(name.toLowerCase());
        return tagRepository.save(existingTag);
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
