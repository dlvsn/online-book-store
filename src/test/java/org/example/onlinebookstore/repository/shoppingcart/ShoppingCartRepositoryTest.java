package org.example.onlinebookstore.repository.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/test/books/insert-books.sql",
        "classpath:database/test/categories/insert-categories.sql",
        "classpath:database/test/books/insert-books-categories.sql",
        "classpath:database/test/users/insert-users.sql",
        "classpath:database/test/shoppingcarts/insert-shoppingcart.sql",
        "classpath:database/test/cartitems/insert-cartitems.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/cartitems/delete-cartitems.sql",
        "classpath:database/test/books/delete-books-categories.sql",
        "classpath:database/test/categories/delete-categories.sql",
        "classpath:database/test/books/delete-books.sql",
        "classpath:database/test/shoppingcarts/delete-shoppingcart.sql",
        "classpath:database/test/users/delete-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ShoppingCartRepositoryTest {
    private static final Long TEST_ID = 7L;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
            Tests the retrieval of a shopping cart by user ID, 
            verifying that the size of the cart items
            matches the expected value. Ensures that 
            the shopping cart and its associated items are fetched
            correctly from the database.
            """)
    void findShoppingCart_byUserId_withCartItemsAndBooks_Success() {
        int expectedCartItemSize = 1;
        int actualCartItemSize = shoppingCartRepository
                .findByUserIdFetchCartItemsAndBooks(TEST_ID)
                .get().getCartItems()
                .size();
        assertEquals(actualCartItemSize, expectedCartItemSize);
    }
}
