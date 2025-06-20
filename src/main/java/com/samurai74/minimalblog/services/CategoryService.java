package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.Category;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listCategories();

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    Category createCategory(Category category);

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    void deleteCategory(UUID categoryId);
    Category getCategoryById(UUID categoryId);

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    Category updateCategory( UUID categoryId, String categoryName);
}
