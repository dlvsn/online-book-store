package org.example.onlybooks.repository.shoppingcart;

import java.util.Optional;
import org.example.onlybooks.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT sc from ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "LEFT JOIN FETCH ci.book WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserIdFetchCartItemsAndBooks(Long userId);
}
