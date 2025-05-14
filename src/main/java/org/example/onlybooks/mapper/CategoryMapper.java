package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.category.CategoryRequestDto;
import org.example.onlybooks.dto.category.CategoryResponseDto;
import org.example.onlybooks.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toModel(CategoryRequestDto categoryDto);

    void updateCategoryFromDto(CategoryRequestDto categoryDto, @MappingTarget Category category);
}
