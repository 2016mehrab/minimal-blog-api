package com.samurai74.minimalblog.mappers;

import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.dtos.CategoryDto;
import com.samurai74.minimalblog.domain.dtos.CreateCategoryRequest;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "postCount", source = "posts",qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);
    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if(posts ==null) return 0;
        return posts.stream().filter(post-> PostStatus.PUBLISHED.equals(post.getStatus())).count();

    }
}
