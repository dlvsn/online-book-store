package org.example.onlinebookstore.repository.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.onlinebookstore.model.OrderItem;
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
        "classpath:database/test/orders/insert-orders.sql",
        "classpath:database/test/orders/insert-order-items.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/books/delete-books-categories.sql",
        "classpath:database/test/categories/delete-categories.sql",
        "classpath:database/test/orders/delete-order-items.sql",
        "classpath:database/test/books/delete-books.sql",
        "classpath:database/test/orders/delete-orders.sql",
        "classpath:database/test/users/delete-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderItemRepositoryTest {
    private static final String EXPECTED_ISBN = "978-3-16-148410-0";
    private static final Long TEST_ORDER_ITEM_ID = 1L;
    private static final Long TEST_USER_ID = 6L;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("""
            Should retrieve an OrderItem by its ID, associated Order ID, and User ID
            """)
    void findOrderItem_byExistingId_Success() {
        OrderItem actual = orderItemRepository.findByIdAndOrderId(TEST_ORDER_ITEM_ID,
                TEST_ORDER_ITEM_ID, TEST_USER_ID).get();

        String actualIsbn = actual.getBook().getIsbn();

        Long actualUserId = actual.getOrder().getUser().getId();
        Long actualOrderItemId = actual.getOrder().getId();

        assertThat(actualIsbn).isEqualTo(EXPECTED_ISBN);
        assertThat(actualUserId).isEqualTo(TEST_USER_ID);
        assertThat(actualOrderItemId).isEqualTo(TEST_ORDER_ITEM_ID);
    }
}
