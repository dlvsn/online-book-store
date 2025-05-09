package org.example.onlinebookstore.repository.category;

import java.util.Set;
import org.example.onlinebookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Set<Category> findByIdIn(Set<Long> ids);
}
