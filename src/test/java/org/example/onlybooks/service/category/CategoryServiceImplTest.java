package org.example.onlybooks.service.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.onlybooks.dto.category.CategoryRequestDto;
import org.example.onlybooks.dto.category.CategoryResponseDto;
import org.example.onlybooks.exception.EntityNotFoundException;
import org.example.onlybooks.mapper.CategoryMapper;
import org.example.onlybooks.model.Category;
import org.example.onlybooks.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Should save a new category successfully")
    void saveCategory_Success() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("test", "test");

        Category category = initCategory();
        category.setId(null);

        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponseDto expected = new CategoryResponseDto(1L, "test", "test");
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.save(categoryRequestDto);

        assertThat(actual).isEqualTo(expected);
        verify(categoryMapper, times(1)).toModel(categoryRequestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("Should return all categories successfully")
    void findAllCategories_Success() {
        Category firstCategory = initCategory();
        Category secondCategory = initCategory();
        secondCategory.setId(2L);
        Category thirdCategory = initCategory();
        thirdCategory.setId(3L);

        List<Category> categories = List.of(firstCategory, secondCategory, thirdCategory);

        CategoryResponseDto firstCategoryResponseDto = new CategoryResponseDto(
                1L, "test", "test");
        CategoryResponseDto secondCategoryResponseDto = new CategoryResponseDto(
                2L, "test", "test");
        CategoryResponseDto thirdCategoryResponseDto = new CategoryResponseDto(
                3L, "test", "test");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(firstCategory)).thenReturn(firstCategoryResponseDto);
        when(categoryMapper.toDto(secondCategory)).thenReturn(secondCategoryResponseDto);
        when(categoryMapper.toDto(thirdCategory)).thenReturn(thirdCategoryResponseDto);

        Set<CategoryResponseDto> expectedCategoryResponseDtos = Set.of(firstCategoryResponseDto,
                secondCategoryResponseDto, thirdCategoryResponseDto);

        Set<CategoryResponseDto> actual = categoryService.findAll(pageable);

        assertThat(actual).isEqualTo(expectedCategoryResponseDtos);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(firstCategory);
        verify(categoryMapper, times(1)).toDto(secondCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Should find category by existing ID successfully")
    void findCategory_withExistingId_Success() {
        Category category = initCategory();
        category.setId(2L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        CategoryResponseDto expected = new CategoryResponseDto(2L, "test", "test");

        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryResponseDto actual = categoryService.findById(category.getId());

        assertThat(actual).isEqualTo(expected);

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Should throw exception when category with non-existing ID is not found")
    void findCategory_withNonExistingId_ThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                categoryService.findById(anyLong()));
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should update category with existing ID successfully")
    void updateCategory_withExistingId_Success() {
        Category category = initCategory();
        String updateName = "new name";
        String updateDescription = "new description";

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Category updatedCategory = initCategory();
        updatedCategory.setName(updateName);
        updatedCategory.setDescription(updateDescription);

        CategoryRequestDto requestDto = new CategoryRequestDto(updateName, updateDescription);
        CategoryResponseDto expected = new CategoryResponseDto(1L, updateName, updateDescription);
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.updateById(category.getId(), requestDto);
        assertThat(actual).isEqualTo(expected);

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryMapper, times(1)).updateCategoryFromDto(requestDto, category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(updatedCategory);
    }

    @Test
    @DisplayName("Should throw exception when updating category with non-existing ID")
    void updateCategory_withNonExistingId_ThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        CategoryRequestDto requestDto = new CategoryRequestDto("new name", "new description");
        Assertions.assertThrows(EntityNotFoundException.class, ()
                -> categoryService.updateById(anyLong(), requestDto));
    }

    @Test
    @DisplayName("Should delete category with existing ID successfully")
    void deleteCategory_withExistingId_Success() {
        Category category = initCategory();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        doNothing().when(categoryRepository).deleteById(category.getId());
        categoryService.deleteById(category.getId());

        verify(categoryRepository, times(1)).deleteById(category.getId());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Test for deleting a category with a non-existing ID, 
            expecting an EntityNotFoundException
            """)
    void deleteCategory_withNonExistingId_ThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, ()
                -> categoryService.deleteById(anyLong()));
    }

    private Category initCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("test");
        category.setDescription("test");
        return category;
    }
}
