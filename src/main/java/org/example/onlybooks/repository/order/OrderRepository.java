package org.example.onlybooks.repository.order;

import java.util.List;
import java.util.Optional;
import org.example.onlybooks.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    List<Order> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
