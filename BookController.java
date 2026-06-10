package com.example.onlinebookstore.book;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping({"/books", "/search"})
    public String books(
        @RequestParam(name = "q", required = false) String q,
        Model model
    ) {
        List<Book> books;
        if (q == null || q.isBlank()) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q);
        }
        model.addAttribute("query", q);
        model.addAttribute("books", books);
        return "books";
    }
}

