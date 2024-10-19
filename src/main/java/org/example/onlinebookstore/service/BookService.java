package org.example.onlinebookstore.service;

import java.util.List;
import org.example.onlinebookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
