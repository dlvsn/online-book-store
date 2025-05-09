package org.example.onlinebookstore.repository.book;

import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.book.BookSearchParameters;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.repository.book.spec.CriteriaSearch;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters bookSearchParams) {
        Specification<Book> specification = Specification.where(null);

        specification = setSpecification(
                specification, bookSearchParams.titles(), CriteriaSearch.TITLE);

        specification = setSpecification(
                specification, bookSearchParams.authors(), CriteriaSearch.AUTHOR);

        specification = setSpecification(
                specification, bookSearchParams.isbn(), CriteriaSearch.ISBN);

        specification = setSpecification(
                specification, bookSearchParams.price(), CriteriaSearch.PRICE);

        specification = setSpecification(
                specification, bookSearchParams.description(), CriteriaSearch.DESCRIPTION);

        specification = setSpecification(
                specification, bookSearchParams.categoryIds(), CriteriaSearch.CATEGORY);

        return specification;
    }

    private Specification<Book> setSpecification(Specification<Book> specification,
                                                 String[] params,
                                                 CriteriaSearch key) {
        if (params != null && params.length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(key.getValue())
                    .getSpecification(params));
        }
        return specification;
    }
}
