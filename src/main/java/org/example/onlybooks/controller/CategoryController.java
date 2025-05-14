package org.example.onlybooks.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlybooks.dto.category.CategoryRequestDto;
import org.example.onlybooks.dto.category.CategoryResponseDto;
import org.example.onlybooks.service.book.BookService;
import org.example.onlybooks.service.category.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category",
            description = "Insert category into db")
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryRequestDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Displays all categories")
    public Set<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id",
            description = "Displays category by id")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by category id",
            description = "Displays all books by category id")
    public List<BookWithoutCategoryIdsResponseDto> getBooksByCategoryId(@PathVariable Long id,
                                                                 Pageable pageable) {
        return bookService.getBooksByCategoryId(id, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update category by id",
            description = "Update category by id")
    public CategoryResponseDto updateCategory(@PathVariable Long id,
                              @RequestBody @Valid CategoryRequestDto categoryDto) {
        return categoryService.updateById(id, categoryDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category from db by id",
            description = "delete category by id")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
