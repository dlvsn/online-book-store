package org.example.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.book.BookResponseDto;
import org.example.onlinebookstore.dto.book.BookSearchParameters;
import org.example.onlinebookstore.dto.book.CreateBookRequestDto;
import org.example.onlinebookstore.service.book.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Online book store", description = "Endpoints for managing books")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new book",
            description = "Inserting the book into DB")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(@RequestBody
                                      @Valid
                                      CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @Operation(summary = "Get all books",
            description = "Displays all sorted available books")
    @GetMapping
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Find book by id",
            description = "Find book by id")
    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable
                                       @Positive Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Find books by parameters",
            description = "Find all books by dynamic parameters")
    @GetMapping("/search")
    public List<BookResponseDto> search(BookSearchParameters params) {
        return bookService.search(params);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update book in db",
            description = "update book in db")
    @PutMapping("/{id}")
    public BookResponseDto updateBook(@PathVariable Long id,
                                      @RequestBody
                                      @Valid
                                      CreateBookRequestDto createRequestBookDto) {
        return bookService.update(id, createRequestBookDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete book",
            description = "Delete book from web-site, but it remains in the database")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable
                           @Positive Long id) {
        bookService.delete(id);
    }
}
