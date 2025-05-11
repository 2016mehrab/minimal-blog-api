package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.Category;

import java.util.List;

public interface CategoryService {
    List<Category> listCategories();
    Category createCategory(Category category);
}
