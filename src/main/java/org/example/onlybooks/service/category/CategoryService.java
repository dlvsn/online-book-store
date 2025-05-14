package org.example.onlybooks.service.category;

import java.util.Set;
import org.example.onlybooks.dto.category.CategoryRequestDto;
import org.example.onlybooks.dto.category.CategoryResponseDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDto save(CategoryRequestDto categoryDto);

    Set<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto findById(Long id);

    CategoryResponseDto updateById(Long id, CategoryRequestDto categoryDto);

    void deleteById(Long id);
}
