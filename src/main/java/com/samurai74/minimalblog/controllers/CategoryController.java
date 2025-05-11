package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.dtos.CategoryDto;
import com.samurai74.minimalblog.domain.dtos.CreateCategoryRequest;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.mappers.CategoryMapper;
import com.samurai74.minimalblog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        var categories=  categoryService.listCategories().stream().map(categoryMapper::toDto).toList();
        return ResponseEntity.ok(categories);
    }
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        var category = categoryMapper.toEntity(createCategoryRequest);
        var savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(categoryMapper.toDto(savedCategory));
    }
}
