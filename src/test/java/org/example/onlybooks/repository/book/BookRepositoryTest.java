package org.example.onlybooks.repository.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            This test method verifies 
            the successful retrieval of a list of books by valid category IDs.
            """)
    @Sql(scripts = {
            "classpath:database/test/books/insert-books.sql",
            "classpath:database/test/categories/insert-categories.sql",
            "classpath:database/test/books/insert-books-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/test/books/delete-books-categories.sql",
            "classpath:database/test/categories/delete-categories.sql",
            "classpath:database/test/books/delete-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findBooksByValidCategoryId_Success() {
        Long categoryId = 1L;

        List<Book> expectedBooks = initExpectedBookList();

        List<Book> actualBooks =
                bookRepository.findAllByCategoriesId(categoryId, Pageable.unpaged());

        assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks);
    }

    private List<Book> initExpectedBookList() {
        return List.of(initFirstBook(), initSecondBook());
    }

    private Category initFirstCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("test name 1");
        category.setDescription("test description 1");
        return category;
    }

    private Category initSecondCategory() {
        Category category = new Category();
        category.setId(2L);
        category.setName("test name 2");
        category.setDescription("test description 2");
        return category;
    }

    private Book initFirstBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test book 1");
        book.setAuthor("Test author 1");
        book.setIsbn("978-3-16-148410-0");
        book.setPrice(BigDecimal.valueOf(99.99));
        book.setDescription("test 1");
        book.setCoverImage(null);
        book.setCategories(Set.of(initFirstCategory(), initSecondCategory()));
        return book;
    }

    private Book initSecondBook() {
        Book book = new Book();
        book.setId(3L);
        book.setTitle("Test book 3");
        book.setAuthor("Test author 3");
        book.setIsbn("978-1-23-456789-0");
        book.setPrice(BigDecimal.valueOf(149.99));
        book.setDescription("test 3");
        book.setCoverImage(null);
        book.setCategories(Set.of(initFirstCategory()));
        return book;
    }
}
