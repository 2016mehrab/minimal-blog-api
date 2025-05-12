package com.samurai74.minimalblog.mappers;

import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.dtos.TagResponse;
import com.samurai74.minimalblog.domain.entities.Post;
import com.samurai74.minimalblog.domain.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel="spring", unmappedTargetPolicy= ReportingPolicy.IGNORE)
public interface TagMapper {
    // there's no postCount field so it has to be generated
    @Mapping(target = "postCount", source ="posts", qualifiedByName = "calculatePostCount")
    TagResponse toResponse(Tag tag);

    List<TagResponse> toTagResponseList(List<Tag> tags);
    @Named("calculatePostCount")
    default int calculatePostCount(Set<Post> posts) {
       if(posts == null) return 0;
       return (int) posts.stream().filter(post-> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
