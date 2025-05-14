package org.example.onlybooks.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.onlybooks.dto.book.BookResponseDto;
import org.example.onlybooks.dto.book.BookSearchParameters;
import org.example.onlybooks.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlybooks.dto.book.CreateBookRequestDto;
import org.example.onlybooks.exception.EntityNotFoundException;
import org.example.onlybooks.mapper.BookMapper;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.model.Category;
import org.example.onlybooks.repository.book.BookRepository;
import org.example.onlybooks.repository.book.BookSpecificationBuilder;
import org.example.onlybooks.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    private static final Long ID = 1L;

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("""
            This test verifies the successful saving of a book with 
            valid data (all required fields are filled) 
            into the database. 
            The method ensures that the book is correctly saved and 
            that the saved record matches the provided data.
            """)
    void saveValidBook_Success() {
        Category category = initCategory();

        CreateBookRequestDto requestDto = initRequestBookDto();

        Book book = initBook(Set.of(category));

        BookResponseDto expected = initResponseDtoBook();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookResponseDto actual = bookService.save(requestDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            This test verifies the retrieval of all books from the database. 
            The method ensures that a list 
            of all books is returned correctly and 
            contains all necessary fields, without any filtering.
            """)
    void getAllBooks_Success() {
        Category category = initCategory();
        Book firstBook = initBook(Set.of(category));
        Book secondBook = initBook(Set.of(category));
        secondBook.setId(2L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(firstBook, secondBook));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        BookResponseDto firstBookResponseDto = initResponseDtoBook();
        BookResponseDto secondBookResponseDto = initResponseDtoBook();

        List<BookResponseDto> expected = List.of(firstBookResponseDto, secondBookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(firstBookResponseDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookResponseDto);

        List<BookResponseDto> actual = bookService.findAll(pageable);

        assertThat(actual).isEqualTo(expected);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the successful retrieval of a book by its existing ID. 
            The method checks that the book with the provided ID is found, 
            and the returned data matches the expected values.
            """)
    void getBook_withExistingId_Success() {
        Book book = initBook(Set.of(initCategory()));

        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));

        BookResponseDto expected = initResponseDtoBook();

        when(bookMapper.toDto(book)).thenReturn(expected);

        BookResponseDto actual = bookService.findById(ID);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findById(ID);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("""
            This test verifies the behavior when attempting to retrieve a book 
            with a non-existing ID. The method is expected to 
            throw an exception (such as EntityNotFoundException), 
            since no book with that ID exists.
            """)
    void getBook_withNonExistingId_ThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(anyLong()));
    }

    @Test
    @DisplayName("""
            This test verifies the successful update of 
            an existing book by its ID. The method ensures that 
            the book's data, such as title, author, price, and categories, 
            is correctly updated in the database.
            """)
    void updateBook_withExistingId_Success() {
        Category firstCategory = initCategory();
        Category secondCategory = initCategory();
        secondCategory.setId(2L);

        String updatedTitle = "updated";
        Set<Category> categories = Set.of(firstCategory, secondCategory);
        Set<Long> categoriesIds = Set.of(firstCategory.getId(), secondCategory.getId());

        CreateBookRequestDto requestDto = initRequestBookDto();
        requestDto.setCategoryIds(categoriesIds);
        requestDto.setTitle(updatedTitle);

        Book book = initBook(Set.of(firstCategory));

        when(categoryRepository.findByIdIn(categoriesIds)).thenReturn(categories);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        BookResponseDto expected = initResponseDtoBook();
        expected.setTitle(updatedTitle);
        expected.setCategoryIds(categoriesIds);

        when(bookMapper.toDto(book)).thenReturn(expected);

        BookResponseDto actual = bookService.update(book.getId(), requestDto);

        assertThat(actual).isEqualTo(expected);

        verify(categoryRepository, times(1)).findByIdIn(categoriesIds);
        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookMapper, times(1)).updateBookFromDto(requestDto, book);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(categoryRepository, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            This test checks the behavior when attempting 
            to update a book with a non-existing ID. The method should throw an exception 
            (such as EntityNotFoundException), since the book with that ID does not exist.
            """)
    void updateBook_withNonExistingId_ThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.update(anyLong(), initRequestBookDto()));
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("""
            This test verifies the successful deletion of a 
            book with an existing ID. 
            The method ensures that the book is deleted 
            from the database and that no record of it remains.
            """)
    void deleteBook_withExistingId_Success() {
        Category category = initCategory();
        Book book = initBook(Set.of(category));

        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).deleteById(anyLong());
        bookService.delete(ID);

        verify(bookRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Test for deleting a book with a non-existing ID, 
            expecting an EntityNotFoundException
            """)
    void deleteBook_withNonExistingId_ThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.delete(ID));
    }

    @Test
    @DisplayName("""
            This test verifies the successful search for books by 
            the author's name. 
            The method checks that books with the specified author are correctly 
            retrieved and returned.
            """)
    void searchBookByAuthor_Success() {
        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .authors(authors())
                .build();

        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setAuthor("Alice");

        Book secondBook = initBook(Set.of(initCategory()));
        secondBook.setAuthor("Bob");

        List<Book> books = List.of(firstBook, secondBook);

        Specification<Book> specification = Mockito.mock(Specification.class);

        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto firstResponseBook = initResponseDtoBook();
        firstResponseBook.setAuthor("Alice");

        BookResponseDto secondResponseBook = initResponseDtoBook();
        secondResponseBook.setAuthor("Bob");

        List<BookResponseDto> expected = List.of(firstResponseBook, secondResponseBook);

        when(bookMapper.toDto(firstBook)).thenReturn(firstResponseBook);
        when(bookMapper.toDto(secondBook)).thenReturn(secondResponseBook);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);

        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the successful search for books by 
            their title. The method checks that books with the specified 
            title are correctly retrieved and returned.
            """)
    void searchBookByTitle_Success() {
        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .titles(titles())
                .build();

        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setTitle("test 2");

        List<Book> books = List.of(firstBook);
        Specification<Book> specification = Mockito.mock(Specification.class);

        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto bookResponseDto = initResponseDtoBook();
        bookResponseDto.setTitle("test 2");

        List<BookResponseDto> expected = List.of(bookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(bookResponseDto);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the behavior of the search method
             when there is only one price parameter. 
            The method ensures that books are correctly
             filtered by the provided price.
            """)
    void searchBookIfPriceParamsLengthIs1_Success() {
        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setPrice(BigDecimal.valueOf(130));

        List<Book> books = List.of(firstBook);
        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .price(new String[] {"200"})
                .build();

        Specification<Book> specification = Mockito.mock(Specification.class);
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto bookResponseDto = initResponseDtoBook();
        bookResponseDto.setPrice(BigDecimal.valueOf(130));

        List<BookResponseDto> expected = List.of(bookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(bookResponseDto);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
             This test verifies the behavior of the search method 
             when a price greater than 2 is provided. 
             The method ensures that books are correctly 
             filtered according to the provided price criteria.
            """)
    void searchBookIfPriceBiggerThan2_Success() {
        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setPrice(BigDecimal.valueOf(300));

        Book secondBook = initBook(Set.of(initCategory()));
        secondBook.setPrice(BigDecimal.valueOf(400));

        Book thirdBook = initBook(Set.of(initCategory()));
        thirdBook.setPrice(BigDecimal.valueOf(600));

        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .price(new String[]{"200, 300, 600"})
                .build();

        List<Book> books = List.of(firstBook, secondBook, thirdBook);

        Specification<Book> specification = Mockito.mock(Specification.class);
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto firstBookResponseDto = initResponseDtoBook();
        firstBookResponseDto.setPrice(BigDecimal.valueOf(300));

        BookResponseDto secondBookResponseDto = initResponseDtoBook();
        secondBookResponseDto.setPrice(BigDecimal.valueOf(400));

        BookResponseDto thirdBookResponseDto = initResponseDtoBook();
        thirdBookResponseDto.setPrice(BigDecimal.valueOf(600));

        List<BookResponseDto> expected = List.of(firstBookResponseDto,
                secondBookResponseDto, thirdBookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(firstBookResponseDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookResponseDto);
        when(bookMapper.toDto(thirdBook)).thenReturn(thirdBookResponseDto);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verify(bookMapper, times(1)).toDto(thirdBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the behavior of the 
            search method when there are two price parameters. 
            The method ensures that books are correctly 
            filtered within the specified price range.
            """)
    void searchBookIfPriceParamsLengthIs2_Success() {
        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .price(prices())
                .build();

        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setPrice(BigDecimal.valueOf(255.55));

        Book secondBook = initBook(Set.of(initCategory()));
        secondBook.setPrice(BigDecimal.valueOf(265.43));

        List<Book> books = List.of(firstBook, secondBook);

        Specification<Book> specification = Mockito.mock(Specification.class);
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto firstBookResponseDto = initResponseDtoBook();
        firstBookResponseDto.setPrice(BigDecimal.valueOf(255.55));

        BookResponseDto secondBookResponseDto = initResponseDtoBook();
        secondBookResponseDto.setPrice(BigDecimal.valueOf(265.43));

        List<BookResponseDto> expected = List.of(firstBookResponseDto, secondBookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(firstBookResponseDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookResponseDto);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the successful search for books by ISBN, 
            description, and category. The method ensures that books 
            matching all the given criteria are correctly retrieved.
            """)
    void searchBookByIsbnDescriptionCategory_Success() {
        Book firstBook = initBook(Set.of(initCategory()));
        firstBook.setDescription("test desc");

        Book secondBook = initBook(Set.of(initCategory()));
        secondBook.setIsbn("978-9-87-654321-0");

        Category category = initCategory();
        category.setId(5L);

        Book thirdBook = initBook(Set.of(category));
        thirdBook.setIsbn("242343333");

        List<Book> books = List.of(firstBook, secondBook, thirdBook);

        BookSearchParameters bookSearchParameters = BookSearchParameters.builder()
                .isbn(isbns())
                .description(descriptions())
                .categoryIds(categoriesIds())
                .build();

        Specification<Book> specification = Mockito.mock(Specification.class);
        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);

        BookResponseDto firstBookResponseDto = initResponseDtoBook();
        firstBookResponseDto.setDescription("test desc");

        BookResponseDto secondBookResponseDto = initResponseDtoBook();
        secondBookResponseDto.setIsbn("978-9-87-654321-0");

        BookResponseDto thirdBookResponseDto = initResponseDtoBook();
        thirdBookResponseDto.setCategoryIds(Set.of(category.getId()));
        thirdBookResponseDto.setIsbn("242343333");

        List<BookResponseDto> expected = List.of(
                firstBookResponseDto,
                secondBookResponseDto,
                thirdBookResponseDto);

        when(bookMapper.toDto(firstBook)).thenReturn(firstBookResponseDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookResponseDto);
        when(bookMapper.toDto(thirdBook)).thenReturn(thirdBookResponseDto);

        List<BookResponseDto> actual = bookService.search(bookSearchParameters);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookSpecificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
        verify(bookMapper, times(1)).toDto(thirdBook);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("""
            This test verifies the successful retrieval of 
            books by category ID. 
            The method checks that books belonging to 
            the specified category are correctly returned.
            """)
    void findBookByCategoryId_Success() {
        Category category = initCategory();
        category.setId(12L);

        Book firstBook = initBook(Set.of(category));

        Pageable pageable = PageRequest.of(0, 10);

        BookWithoutCategoryIdsResponseDto
                bookWithoutCategoryIdsResponseDto = initBookWithoutCategory();

        List<Book> books = List.of(firstBook);

        List<BookWithoutCategoryIdsResponseDto>
                expected = List.of(bookWithoutCategoryIdsResponseDto);

        when(bookRepository.findAllByCategoriesId(category.getId(), pageable))
                .thenReturn(books);

        when(bookMapper.toWithoutCategoryIdDto(firstBook))
                .thenReturn(bookWithoutCategoryIdsResponseDto);

        List<BookWithoutCategoryIdsResponseDto> actual = bookService.getBooksByCategoryId(
                category.getId(), pageable);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findAllByCategoriesId(category.getId(), pageable);
        verify(bookMapper, times(1)).toWithoutCategoryIdDto(firstBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private Book initBook(Set<Category> categories) {
        Book book = new Book();
        book.setId(ID);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setIsbn("978-0-12-345678-9");
        book.setPrice(BigDecimal.valueOf(10099.99));
        book.setDescription("test description");
        book.setCoverImage(null);
        book.setCategories(categories);
        return book;
    }

    private Category initCategory() {
        Category category = new Category();
        category.setId(ID);
        category.setName("test category");
        category.setDescription("test description");
        return category;
    }

    private BookResponseDto initResponseDtoBook() {
        BookResponseDto responseDto = new BookResponseDto();
        responseDto.setTitle("test title");
        responseDto.setAuthor("test author");
        responseDto.setIsbn("978-0-12-345678-9");
        responseDto.setPrice(BigDecimal.valueOf(10099.99));
        responseDto.setDescription("test description");
        responseDto.setCoverImage(null);
        responseDto.setCategoryIds(Set.of(ID));
        return responseDto;
    }

    private BookWithoutCategoryIdsResponseDto initBookWithoutCategory() {
        return new BookWithoutCategoryIdsResponseDto(
                "test title",
                "test author",
                "978-0-12-345678-9",
                BigDecimal.valueOf(10099.99),
                "test description",
                null
        );
    }

    private String[] titles() {
        return new String[]{"test 1", "test 2"};
    }

    private String[] authors() {
        return new String[] {"Bob", "Alice"};
    }

    private String[] isbns() {
        return new String [] {"978-0-12-345678-9", "978-9-87-654321-0"};
    }

    private String[] prices() {
        return new String[] {"200", "500"};
    }

    private String[] descriptions() {
        return new String[] {"test desc", "test desc"};
    }

    private String[] categoriesIds() {
        return new String[] {"1", "2", "3"};
    }

    private CreateBookRequestDto initRequestBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("test title");
        requestDto.setAuthor("test author");
        requestDto.setIsbn("978-0-12-345678-9");
        requestDto.setPrice(BigDecimal.valueOf(10099.99));
        requestDto.setDescription("test description");
        requestDto.setCoverImage(null);
        requestDto.setCategoryIds(Set.of(ID));
        return requestDto;
    }
}
