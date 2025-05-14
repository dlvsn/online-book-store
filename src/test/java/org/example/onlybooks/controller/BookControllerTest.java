package org.example.onlybooks.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.example.onlybooks.dto.book.BookResponseDto;
import org.example.onlybooks.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "classpath:database/test/books/insert-books.sql",
        "classpath:database/test/categories/insert-categories.sql",
        "classpath:database/test/books/insert-books-categories.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/books/delete-books-categories.sql",
        "classpath:database/test/books/delete-books.sql",
        "classpath:database/test/categories/delete-categories.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookControllerTest {
    private static final Long INVALID_ID = 50L;
    private static final Long TEST_ID = 2L;
    private static final String BOOK_ENDPOINT = "/books";
    private static final String BOOK_ID_ENDPOINT = "/books/{id}";

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
              .apply(springSecurity())
              .build();
    }

    @Test
    @DisplayName("""
            Tests the creation of a book with valid request data by an admin user.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = initRequestBookDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post(BOOK_ENDPOINT)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        BookResponseDto expectedResponseDto = initBookResponseDto(requestDto);

        BookResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(),
                BookResponseDto.class);

        Assertions.assertEquals(expectedResponseDto, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_InvalidRequestDto_BadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = initInvalidRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(
                post(BOOK_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    @DisplayName("""
            Tests fetching a list of all books by 
            a user with the "USER" role, expecting a list of books.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void getAllBooks_AsUser_ReturnsListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(
                get(BOOK_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
        )
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<BookResponseDto> expected = initListBookResponseDto();
        List<BookResponseDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {});
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBook_withNonExistingId_NotFound() throws Exception {
        mockMvc.perform(
                get(BOOK_ID_ENDPOINT, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Tests fetching a specific book by its ID for a user 
            with the "USER" role, expecting the correct book data.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_withExistingId_ReturnsBook() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(BOOK_ID_ENDPOINT, TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                        .andExpect(status().isOk())
                        .andReturn();

        BookResponseDto expected = initSecondBookResponseDto();
        BookResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Tests updating an existing book by its ID with 
            valid data as an admin user, expecting the updated book data.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ValidIdAndRequest_UpdatesAndReturnsBook() throws Exception {
        CreateBookRequestDto requestDto = updateSecondBook();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put(BOOK_ID_ENDPOINT, TEST_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                        .andExpect(status().isOk())
                        .andReturn();

        BookResponseDto expected = initBookResponseDto(requestDto);

        BookResponseDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(),
                BookResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test for updating a book with a non-existing ID, 
            expecting a 404 Not Found response
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_withNonExistingId_NotFound() throws Exception {
        CreateBookRequestDto createBookRequestDto = updateSecondBook();
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        mockMvc.perform(
                put(BOOK_ID_ENDPOINT, INVALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Should return 400 Bad Request 
            when admin tries to update a book with an invalid request
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_withInvalidRequestDto_BadRequest() throws Exception {
        CreateBookRequestDto requestDto = initInvalidRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                put(BOOK_ID_ENDPOINT, TEST_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("""
            Tests searching for books using 
            valid parameters, expecting a filtered list of books.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void search_ValidParams_ReturnsListOfBooks() throws Exception {
        String[] titleParams = {"Test book 2"};
        String[] priceParams = {"130", "152"};

        MvcResult result = mockMvc.perform(
                get("/books/search")
                        .param("price", priceParams)
                        .param("titles", titleParams)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                        .andExpect(status().isOk())
                        .andReturn();

        List<BookResponseDto> expected = List.of(initSecondBookResponseDto());
        String jsonResponse = result.getResponse().getContentAsString();

        List<BookResponseDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {});

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Tests the deletion of a book by its ID 
            by an admin user, expecting a No Content status.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_asAdmin_Success() throws Exception {
        mockMvc.perform(
                delete(BOOK_ID_ENDPOINT, TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            Should return 404 Not Found when
            admin tries to delete a book with a non-existing ID
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_asAdminWithNonExistingId_NotFound() throws Exception {
        mockMvc.perform(
                delete(BOOK_ID_ENDPOINT, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    private CreateBookRequestDto initRequestBookDto() {
        return new CreateBookRequestDto()
                .setTitle("Test title")
                .setAuthor("Test author")
                .setPrice(BigDecimal.valueOf(59.95))
                .setIsbn("978-0-525-56080-2")
                .setDescription("valid book description")
                .setCoverImage(null)
                .setCategoryIds(Set.of(2L));
    }

    private BookResponseDto initBookResponseDto(CreateBookRequestDto requestDto) {
        return new BookResponseDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setPrice(requestDto.getPrice())
                .setIsbn(requestDto.getIsbn())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds());
    }

    private CreateBookRequestDto updateSecondBook() {
        return new CreateBookRequestDto()
                .setTitle("updated title")
                .setAuthor("updated author")
                .setPrice(BigDecimal.valueOf(180.22))
                .setIsbn("978-0-12-345678-9")
                .setDescription("test description")
                .setCoverImage(null)
                .setCategoryIds(Set.of(1L, 2L));
    }

    private BookResponseDto initFirstBookResponseDto() {
        return new BookResponseDto()
                .setTitle("Test book 1")
                .setAuthor("Test author 1")
                .setPrice(BigDecimal.valueOf(99.99))
                .setIsbn("978-3-16-148410-0")
                .setDescription("test 1")
                .setCoverImage(null)
                .setCategoryIds(Set.of(1L, 2L));
    }

    private BookResponseDto initSecondBookResponseDto() {
        return new BookResponseDto()
                .setTitle("Test book 2")
                .setAuthor("Test author 2")
                .setPrice(BigDecimal.valueOf(151.22))
                .setIsbn("978-0-12-345678-9")
                .setDescription("test 2")
                .setCoverImage(null)
                .setCategoryIds(Set.of(2L));
    }

    private BookResponseDto initThirdBookResponseDto() {
        return new BookResponseDto()
                .setTitle("Test book 3")
                .setAuthor("Test author 3")
                .setPrice(BigDecimal.valueOf(149.99))
                .setIsbn("978-1-23-456789-0")
                .setDescription("test 3")
                .setCoverImage(null)
                .setCategoryIds(Set.of(1L));
    }

    private BookResponseDto initFourthBookResponseDto() {
        return new BookResponseDto()
                .setTitle("Test book 4")
                .setAuthor("Test author 4")
                .setPrice(BigDecimal.valueOf(250.99))
                .setIsbn("978-9-87-654321-0")
                .setDescription("test 4")
                .setCoverImage(null)
                .setCategoryIds(Set.of(2L));
    }

    private List<BookResponseDto> initListBookResponseDto() {
        return List.of(
                initFirstBookResponseDto(),
                initSecondBookResponseDto(),
                initThirdBookResponseDto(),
                initFourthBookResponseDto());
    }

    private CreateBookRequestDto initInvalidRequestDto() {
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("");
        request.setTitle("");
        request.setIsbn("2345");
        request.setPrice(BigDecimal.valueOf(0));
        request.setDescription("hello world");
        request.setCategoryIds(Collections.emptySet());
        return request;
    }
}
