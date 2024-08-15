package org.example.nerdysoft.input.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.dto.BookDto;
import org.example.nerdysoft.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<BookDto> getAllBooks() {
        log.info("Fetching all books");
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        log.info("Fetching book by id: {}", id);
        BookDto book = bookService.getBookById(id);
        if (book == null) {
            log.error("Book not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public BookDto createBook(@RequestBody BookDto book) {
        log.info("Creating book: {}", book);
        return bookService.saveBook(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDetails) {
        log.info("Updating book with id: {}", id);
        BookDto book = bookService.getBookById(id);
        if (book == null) {
            log.error("Book not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setAmount(bookDetails.getAmount());
        return ResponseEntity.ok(bookService.saveBook(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borrow")
    public ResponseEntity<Void> borrowBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        log.info("Member with id: {} borrowing book with id: {}", memberId, bookId);
        bookService.borrowBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        log.info("Member with id: {} returning book with id: {}", memberId, bookId);
        bookService.returnBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/borrowed/{memberName}")
    public Set<BookDto> getBooksBorrowedByMember(@PathVariable String memberName) {
        log.info("Fetching books borrowed by member: {}", memberName);
        return bookService.getBooksBorrowedByMember(memberName);
    }

    @GetMapping("/borrowed/distinct")
    public Set<String> getDistinctBorrowedBookNames() {
        log.info("Fetching distinct borrowed book names");
        return bookService.getDistinctBorrowedBookNames();
    }

    @GetMapping("/borrowed/distinct/amount")
    public Map<String, Long> getDistinctBorrowedBookNamesWithAmount() {
        log.info("Fetching distinct borrowed book names with amount");
        return bookService.getDistinctBorrowedBookNamesWithAmount();
    }
}