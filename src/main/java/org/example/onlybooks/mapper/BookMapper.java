package org.example.onlybooks.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.book.BookResponseDto;
import org.example.onlybooks.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlybooks.dto.book.CreateBookRequestDto;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookResponseDto toDto(Book book);

    BookWithoutCategoryIdsResponseDto toWithoutCategoryIdDto(Book book);

    @AfterMapping
    default void setCategoriesIds(@MappingTarget BookResponseDto bookDto, Book book) {
        if (book.getCategories() != null) {
            bookDto.setCategoryIds(book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
        }
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto bookDto) {
        Set<Category> categories = bookDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    Book toModel(CreateBookRequestDto bookDto);

    void updateBookFromDto(CreateBookRequestDto bookDto, @MappingTarget Book book);
}
