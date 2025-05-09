package org.example.onlinebookstore.service.category;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.category.CategoryRequestDto;
import org.example.onlinebookstore.dto.category.CategoryResponseDto;
import org.example.onlinebookstore.exception.EntityNotFoundException;
import org.example.onlinebookstore.mapper.CategoryMapper;
import org.example.onlinebookstore.model.Category;
import org.example.onlinebookstore.repository.category.CategoryRepository;
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
