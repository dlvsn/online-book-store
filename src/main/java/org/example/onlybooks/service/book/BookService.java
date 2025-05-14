package org.example.onlybooks.service.book;

import java.util.List;
import org.example.onlybooks.dto.book.BookResponseDto;
import org.example.onlybooks.dto.book.BookSearchParameters;
import org.example.onlybooks.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlybooks.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto save(CreateBookRequestDto requestBookDto);

    List<BookResponseDto> findAll(Pageable pageable);

    BookResponseDto findById(Long id);

    BookResponseDto update(Long id, CreateBookRequestDto bookDto);

    void delete(Long id);

    List<BookResponseDto> search(BookSearchParameters params);

    List<BookWithoutCategoryIdsResponseDto> getBooksByCategoryId(Long id, Pageable pageable);
}
