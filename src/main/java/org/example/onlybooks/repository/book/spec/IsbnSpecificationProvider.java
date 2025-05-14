package org.example.onlybooks.repository.book.spec;

import java.util.Arrays;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    private static final String ISBN = CriteriaSearch.ISBN.getValue();

    @Override
    public String getKey() {
        return ISBN;
    }

    @Override
    public Specification getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(ISBN).in(Arrays.stream(params).toArray());
    }
}
