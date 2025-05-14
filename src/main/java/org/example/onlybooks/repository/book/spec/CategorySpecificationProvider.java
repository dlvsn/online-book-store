package org.example.onlybooks.repository.book.spec;

import jakarta.persistence.criteria.Join;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.model.Category;
import org.example.onlybooks.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategorySpecificationProvider implements SpecificationProvider<Book> {
    private static final String CATEGORIES = "categories";
    private static final String ID = "id";

    @Override
    public String getKey() {
        return CriteriaSearch.CATEGORY.getValue();
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Set<Long> categoryIds = Arrays.stream(params)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            Join<Book, Category> categoriesJoin = root.join(CATEGORIES);

            return categoriesJoin.get(ID).in(categoryIds);
        };
    }
}
