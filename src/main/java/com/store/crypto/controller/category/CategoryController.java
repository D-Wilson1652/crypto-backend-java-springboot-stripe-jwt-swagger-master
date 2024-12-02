package com.store.crypto.controller.category;

import com.store.crypto.model.category.Category;
import com.store.crypto.service.category.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> createCategory(@Valid @RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getCategories() {
        return categoryService.getCategories();
    }

    @SecurityRequirement(name = "Authorization")
    @PutMapping("/update")
    public ResponseEntity<Object> updateCategory(@RequestBody Category updatedCategory) {
        return categoryService.updateCategory(updatedCategory);
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/delete/{categoryId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Integer categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/get/listings")
    public ResponseEntity<Object> getCategoryById(@RequestParam(name = "categoryName") String categoryName,
                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return categoryService.getCategoriesByName(categoryName, pageNumber, pageSize);
    }

}
