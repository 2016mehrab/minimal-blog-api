package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.repositories.CategoryRepository;
import com.samurai74.minimalblog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public List<Category> listCategories() {

        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category with name " + category.getName() + " already exists");
        }
        return categoryRepository.save(category);
    }
}
