package com.samurai74.minimalblog.mappers;

import com.samurai74.minimalblog.domain.CreatePostRequest;
import com.samurai74.minimalblog.domain.UpdatePostRequest;
import com.samurai74.minimalblog.domain.dtos.CreatePostRequestDto;
import com.samurai74.minimalblog.domain.dtos.PostDto;
import com.samurai74.minimalblog.domain.dtos.UpdatePostRequestDto;
import com.samurai74.minimalblog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toPostDto(Post post);
    CreatePostRequest toCreatePostRequest(CreatePostRequestDto postRequestDto);
    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto postRequestDto);
}
