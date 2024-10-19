package org.example.onlinebookstore.repository;

import java.util.List;
import org.example.onlinebookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
