package org.example.onlinebookstore.repository.shoppingcart;

import java.util.Optional;
import org.example.onlinebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);

    void deleteCartItemByIdAndShoppingCartId(Long id, Long shoppingCartId);
}
