package org.example.onlybooks.repository.book.spec;

import java.util.Arrays;
import org.example.onlybooks.model.Book;
import org.example.onlybooks.repository.book.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE = CriteriaSearch.TITLE.getValue();

    @Override
    public String getKey() {
        return TITLE;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(TITLE).in(Arrays.stream(params).toArray());
    }
}
