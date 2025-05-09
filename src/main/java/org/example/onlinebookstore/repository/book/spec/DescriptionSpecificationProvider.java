package org.example.onlinebookstore.repository.book.spec;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    private static final String DESCRIPTION = CriteriaSearch.DESCRIPTION.getValue();

    @Override
    public String getKey() {
        return DESCRIPTION;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Arrays.stream(params)
                    .map(p -> criteriaBuilder.like(root.get(DESCRIPTION), "%" + p + "%"))
                    .toList();
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
