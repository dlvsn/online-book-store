package org.example.onlybooks.repository.order;

import java.util.Optional;
import org.example.onlybooks.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem "
            + "oi JOIN oi.order o "
            + "WHERE oi.id = :id AND o.id = :orderId AND o.user.id = :userId")
    Optional<OrderItem> findByIdAndOrderId(@Param("orderId") Long orderId,
                                           @Param("id") Long id,
                                           @Param("userId") Long userId);
}
