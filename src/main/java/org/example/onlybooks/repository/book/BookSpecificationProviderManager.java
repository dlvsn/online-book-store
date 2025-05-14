package org.example.onlybooks.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.exception.SpecificationNotFoundException;
import org.example.onlybooks.model.Book;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new SpecificationNotFoundException("Specification by key "
                        + key
                        + " not found"));
    }
}
