package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.domain.dtos.CategoryDto;
import com.samurai74.minimalblog.domain.dtos.CreateCategoryRequest;
import com.samurai74.minimalblog.mappers.CategoryMapper;
import com.samurai74.minimalblog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
