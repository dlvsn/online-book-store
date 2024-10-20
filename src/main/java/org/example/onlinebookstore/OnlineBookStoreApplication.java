package org.example.onlinebookstore;

import java.math.BigDecimal;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Book Title");
            book.setAuthor("Author");
            book.setIsbn("1242524");
            book.setPrice(BigDecimal.valueOf(100));
            book.setDescription("Book Description");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
