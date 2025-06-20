package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.PostStatus;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.repositories.CategoryRepository;
import com.samurai74.minimalblog.repositories.PostRepository;
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
    private final PostRepository postRepository;


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

        var category  =categoryRepository.findById(categoryId).orElseThrow(()->new EntityNotFoundException("category with id " + categoryId + " not found"));

             if (!category.getPosts().isEmpty()) {
                 var uncategorized = categoryRepository.findByNameIgnoreCase("uncategorized").orElseThrow(()->new IllegalStateException("first create an 'uncategorized' category" ));
                  postRepository.findAllByCategory_Id(categoryId).stream().peek((post) -> {
                     if (!post.getStatus().equals(PostStatus.DRAFT))
                         throw new IllegalStateException("Cannot delete category '" + category.getName() + "' as it is associated with published or pending posts. Please unpublish/reject those posts or remove the category from them before deleting the category.");
                 }).peek(post-> post.setCategory(uncategorized)).toList();
             }
             category.setPosts(null);
             categoryRepository.deleteById(categoryId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()->new EntityNotFoundException("category with id " + categoryId + " not found"));
    }


    @Override
    @Transactional
    public Category updateCategory(UUID categoryId, String categoryName) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));

        //  the user is just saving the same name for the same category , then no error
        if (categoryRepository.existsByNameIgnoreCase(categoryName) && !existingCategory.getName().equalsIgnoreCase(categoryName)) {
            throw new IllegalArgumentException("Category with name " + categoryName + " already exists");
        }
        existingCategory.setName(categoryName);
        return categoryRepository.save(existingCategory);
    }
}
