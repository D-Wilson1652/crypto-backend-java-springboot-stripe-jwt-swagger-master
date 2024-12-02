package com.store.crypto.service.category;

import com.store.crypto.model.category.Category;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<Object> createCategory(Category category);

    ResponseEntity<Object> getCategories();

    ResponseEntity<Object> updateCategory(Category category);

    ResponseEntity<Object> deleteCategory(Integer categoryId);

    ResponseEntity<Object> getCategoriesByName(String name, int pageNumber, int pageSize);
}
