package org.example.onlinebookstore.service.book;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.book.BookResponseDto;
import org.example.onlinebookstore.dto.book.BookSearchParameters;
import org.example.onlinebookstore.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlinebookstore.dto.book.CreateBookRequestDto;
import org.example.onlinebookstore.exception.EntityNotFoundException;
import org.example.onlinebookstore.mapper.BookMapper;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.model.Category;
import org.example.onlinebookstore.repository.book.BookRepository;
import org.example.onlinebookstore.repository.book.BookSpecificationBuilder;
import org.example.onlinebookstore.repository.category.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookResponseDto save(CreateBookRequestDto createRequestBookDto) {
        Book newBook = bookMapper.toModel(createRequestBookDto);
        return bookMapper.toDto(bookRepository.save(newBook));
    }

    @Override
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookResponseDto findById(Long id) {
        Book bookFromDb = findBookById(id);
        return bookMapper.toDto(bookFromDb);
    }

    @Override
    public BookResponseDto update(Long id, CreateBookRequestDto bookDto) {
        Set<Category> categories = categoryRepository.findByIdIn(bookDto.getCategoryIds());
        Book bookFromDb = findBookById(id);
        bookFromDb.setCategories(categories);
        bookMapper.updateBookFromDto(bookDto, bookFromDb);
        return bookMapper.toDto(bookRepository.save(bookFromDb));
    }

    @Override
    public void delete(Long id) {
        findBookById(id);
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookResponseDto> search(BookSearchParameters params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookWithoutCategoryIdsResponseDto> getBooksByCategoryId(Long id,
                                                                        Pageable pageable) {
        return bookRepository.findAllByCategoriesId(id, pageable).stream()
                .map(bookMapper::toWithoutCategoryIdDto)
                .toList();
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find book by id: " + id));
    }
}
