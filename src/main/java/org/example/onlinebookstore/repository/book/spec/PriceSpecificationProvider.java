package org.example.onlinebookstore.repository.book.spec;

import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE = CriteriaSearch.PRICE.getValue();

    @Override
    public String getKey() {
        return PRICE;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            switch (params.length) {
                case 1:
                    return criteriaBuilder.lessThanOrEqualTo(
                            root.get(PRICE), params[0]);
                case 2:
                    return criteriaBuilder.between(
                            root.get(PRICE), params[0], params[1]);
                default:
                    return criteriaBuilder.lessThanOrEqualTo(
                            root.get(PRICE), params[params.length - 1]);
            }
        };
    }
}
