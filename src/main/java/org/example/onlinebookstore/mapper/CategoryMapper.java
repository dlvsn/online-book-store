package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.category.CategoryRequestDto;
import org.example.onlinebookstore.dto.category.CategoryResponseDto;
import org.example.onlinebookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toModel(CategoryRequestDto categoryDto);

    void updateCategoryFromDto(CategoryRequestDto categoryDto, @MappingTarget Category category);
}
