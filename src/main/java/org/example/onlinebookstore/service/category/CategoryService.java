package org.example.onlinebookstore.service.category;

import java.util.Set;
import org.example.onlinebookstore.dto.category.CategoryRequestDto;
import org.example.onlinebookstore.dto.category.CategoryResponseDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDto save(CategoryRequestDto categoryDto);

    Set<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto findById(Long id);

    CategoryResponseDto updateById(Long id, CategoryRequestDto categoryDto);

    void deleteById(Long id);
}
