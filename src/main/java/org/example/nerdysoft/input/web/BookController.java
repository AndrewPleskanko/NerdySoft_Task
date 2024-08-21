package org.example.nerdysoft.input.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.dto.BookMainInfoDto;
import org.example.nerdysoft.model.dto.BorrowRequestDto;
import org.example.nerdysoft.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDetailedDto> getAllBooks() {
        log.info("Fetching all books");
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailedDto> getBookById(@PathVariable Long id) {
        log.info("Fetching book by id: {}", id);
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDetailedDto> createBook(@Valid @RequestBody BookDetailedDto book) {
        log.info("Creating book: {}", book);
        return ResponseEntity.ok(bookService.saveBook(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDetailedDto> updateBook(@PathVariable Long id,
                                                      @Valid @RequestBody BookDetailedDto bookDetails) {
        log.info("Updating book with id: {}", id);
        return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borrow")
    public ResponseEntity<Void> borrowBook(@RequestBody BorrowRequestDto borrowRequest) {
        log.info("Processing borrow request: {}", borrowRequest);
        bookService.borrowBook(borrowRequest);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/return")
    public ResponseEntity<Void> returnBook(@RequestBody BorrowRequestDto borrowRequest) {
        log.info("Processing return request: {}", borrowRequest);
        bookService.returnBook(borrowRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/borrowed/{memberName}")
    public ResponseEntity<Set<BookMainInfoDto>> getBooksBorrowedByMember(@PathVariable String memberName) {
        log.info("Fetching books borrowed by member: {}", memberName);
        return ResponseEntity.ok(bookService.getBooksBorrowedByMember(memberName));
    }

    @GetMapping("/borrowed/distinct")
    public ResponseEntity<Set<String>> getDistinctBorrowedBookNames() {
        log.info("Fetching distinct borrowed book names");
        return ResponseEntity.ok(bookService.getDistinctBorrowedBookNames());
    }

    @GetMapping("/borrowed/distinct/amounts")
    public ResponseEntity<Map<String, Long>> getDistinctBorrowedBookNamesWithAmount() {
        log.info("Fetching distinct borrowed book names with amount");
        return ResponseEntity.ok(bookService.getDistinctBorrowedBookNamesWithAmount());
    }
}
