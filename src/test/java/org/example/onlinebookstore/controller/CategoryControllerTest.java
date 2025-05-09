package org.example.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.example.onlinebookstore.dto.book.BookWithoutCategoryIdsResponseDto;
import org.example.onlinebookstore.dto.category.CategoryRequestDto;
import org.example.onlinebookstore.dto.category.CategoryResponseDto;
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
public class CategoryControllerTest {
    public static final Long INVALID_ID = 50L;
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final String CATEGORY_ENDPOINT = "/categories";
    private static final String CATEGORY_ENDPOINT_ID = "/categories/{id}";
    private static final String CATEGORY_ID_BOOK_ENDPOINT = "/categories/{id}/books";

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
            Verifies that an admin user can successfully 
            add a new category with valid input, returning the created category's details.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addCategory_asAdmin_Success() throws Exception {
        CategoryRequestDto categoryRequestDto = initCategoryRequest();

        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);
        MvcResult result = mockMvc.perform(
                post(CATEGORY_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        CategoryResponseDto expected = new CategoryResponseDto(
                3L,
                "Test category name",
                "Test category description");

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        When an admin tries to add a category with an invalid request, 
        the server should respond with HTTP status 400 (Bad Request).
                """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addCategory_asAdminWithInvalidRequest_BadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = initInvalidRequest();
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);
        mockMvc.perform(
                post(CATEGORY_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Tests that a user with the role USER can 
            retrieve a list of all available categories successfully.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void getAllCategories_asUser_ReturnListOfCategories() throws Exception {
        MvcResult result = mockMvc.perform(
                get(CATEGORY_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Set<CategoryResponseDto> expected = Set.of(initFirstCategory(), initSecondCategory());
        Set<CategoryResponseDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verifies that a user with the role USER can
             retrieve a specific category by its ID.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void findCategoryById_withExistingId_ReturnCategory() throws Exception {
        MvcResult result = mockMvc.perform(
                get(CATEGORY_ENDPOINT_ID, TEST_CATEGORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        CategoryResponseDto expected = initFirstCategory();

        String jsonResponse = result.getResponse().getContentAsString();

        CategoryResponseDto actual = objectMapper.readValue(jsonResponse,
                CategoryResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        When a user with the 'USER' role tries to find a category by a non-existing ID, 
        the server should respond with HTTP status 404 (Not Found).
                """)
    @WithMockUser(username = "user", roles = "USER")
    void findCategoryById_withNonExistingId_NotFound() throws Exception {
        mockMvc.perform(
                get(CATEGORY_ENDPOINT_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Tests that a user with the role USER can retrieve
             a list of books associated with a specific category ID.
            """)
    @WithMockUser(username = "user", roles = "USER")
    void findBookByCategoryId_withExistingId_ReturnBook() throws Exception {
        MvcResult result = mockMvc.perform(get(CATEGORY_ID_BOOK_ENDPOINT, TEST_CATEGORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<BookWithoutCategoryIdsResponseDto> expected = List.of(
                initFirstBook(), initSecondBook());
        List<BookWithoutCategoryIdsResponseDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {});

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        When a user with the 'USER' role tries to find books by a non-existing category ID, 
        the server should respond with HTTP status 200 (OK) and an empty list in the response body.
                """)
    @WithMockUser(username = "user", roles = "USER")
    void findBookByCategoryId_withNonExistingId_ReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(
                get(CATEGORY_ID_BOOK_ENDPOINT, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<BookWithoutCategoryIdsResponseDto> expected = Collections.emptyList();
        List<BookWithoutCategoryIdsResponseDto> actual = objectMapper.readValue(jsonResponse,
                new TypeReference<>() {});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verifies that an admin user can successfully 
            update the details of an existing category with valid input.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_asAdmin_Success() throws Exception {
        CategoryRequestDto categoryRequestDto = initCategoryRequest();
        String jsonRequest = objectMapper.writeValueAsString(initCategoryRequest());

        MvcResult result = mockMvc.perform(
                put(CATEGORY_ENDPOINT_ID, TEST_CATEGORY_ID)
                .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        CategoryResponseDto expected = new CategoryResponseDto(
                TEST_CATEGORY_ID,
                categoryRequestDto.name(),
                categoryRequestDto.description());

        String jsonResponse = result.getResponse().getContentAsString();
        CategoryResponseDto actual = objectMapper.readValue(jsonResponse,
                CategoryResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test for updating a category with a non-existing ID as an admin, 
            expecting a 404 Not Found response
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_asAdminWithNonExistingId_NotFound() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(initCategoryRequest());
        mockMvc.perform(
                put(CATEGORY_ENDPOINT_ID, INVALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Should return 400 Bad Request 
            when admin tries to update a category with an invalid request
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_asAdminWithInvalidRequest_BadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = initInvalidRequest();
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);
        mockMvc.perform(
                put(CATEGORY_ENDPOINT_ID, TEST_CATEGORY_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Tests that an admin user can successfully 
            delete an existing category, returning a 204 status.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_asAdmin_Success() throws Exception {
        mockMvc.perform(
                delete(CATEGORY_ENDPOINT_ID, TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            Should return 404 Not Found when
             admin tries to delete a category with a non-existing ID
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_asAdminWithNonExistingId_NotFound() throws Exception {
        mockMvc.perform(
                delete(CATEGORY_ENDPOINT_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    private CategoryRequestDto initInvalidRequest() {
        return new CategoryRequestDto("", "23");
    }

    private CategoryRequestDto initCategoryRequest() {
        return new CategoryRequestDto(
                "Test category name",
                "Test category description");
    }

    private BookWithoutCategoryIdsResponseDto initFirstBook() {
        return new BookWithoutCategoryIdsResponseDto(
                "Test book 1",
                "Test author 1",
                "978-3-16-148410-0",
                BigDecimal.valueOf(99.99),
                "test 1",
                null);
    }

    private BookWithoutCategoryIdsResponseDto initSecondBook() {
        return new BookWithoutCategoryIdsResponseDto(
                "Test book 3",
                "Test author 3",
                "978-1-23-456789-0",
                BigDecimal.valueOf(149.99),
                "test 3",
                null);
    }

    private CategoryResponseDto initFirstCategory() {
        return new CategoryResponseDto(
                1L,
                "test name 1",
                "test description 1"
        );
    }

    private CategoryResponseDto initSecondCategory() {
        return new CategoryResponseDto(
                2L,
                "test name 2",
                "test description 2"
        );
    }
}
