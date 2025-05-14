package org.example.onlybooks.service.category;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.dto.category.CategoryRequestDto;
import org.example.onlybooks.dto.category.CategoryResponseDto;
import org.example.onlybooks.exception.EntityNotFoundException;
import org.example.onlybooks.mapper.CategoryMapper;
import org.example.onlybooks.model.Category;
import org.example.onlybooks.repository.category.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public Set<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        Category category = findCategoryById(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryResponseDto updateById(Long id, CategoryRequestDto categoryDto) {
        Category categoryFromDb = findCategoryById(id);
        categoryMapper.updateCategoryFromDto(categoryDto, categoryFromDb);
        return categoryMapper.toDto(categoryRepository.save(categoryFromDb));
    }

    @Override
    public void deleteById(Long id) {
        Category categoryById = findCategoryById(id);
        categoryRepository.deleteById(categoryById.getId());
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find category with id: " + id));
    }
}
