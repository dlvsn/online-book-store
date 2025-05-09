package org.example.onlinebookstore.repository.book.spec;

import java.util.Arrays;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String AUTHOR = CriteriaSearch.AUTHOR.getValue();

    @Override
    public String getKey() {
        return AUTHOR;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(AUTHOR).in(Arrays.stream(params).toArray());
    }
}
