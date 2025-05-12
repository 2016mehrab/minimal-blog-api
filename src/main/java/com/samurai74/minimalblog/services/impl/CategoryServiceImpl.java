package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.repositories.CategoryRepository;
import com.samurai74.minimalblog.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
         Optional<Category>categoryOpt =categoryRepository.findById(categoryId);
         if (categoryOpt.isPresent()) {
             if (!categoryOpt.get().getPosts().isEmpty()) {
                throw new IllegalStateException("Category has posts associated with it");
             }
             categoryRepository.deleteById(categoryId);
         }
    }
    @Transactional(readOnly = true)
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()->new EntityNotFoundException("category with id " + categoryId + " not found"));
    }
}
