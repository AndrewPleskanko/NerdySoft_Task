package org.example.nerdysoft.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.dto.BookDetailedDto;

public interface BookService {
    List<BookDetailedDto> getAllBooks();

    BookDetailedDto updateBook(Long id, BookDetailedDto bookDetails);

    BookDetailedDto getBookById(Long id);

    BookDetailedDto saveBook(BookDetailedDto bookDetailedDto);

    void deleteBook(Long id);

    void borrowBook(Long memberId, Long bookId);

    void returnBook(Long memberId, Long bookId);

    Set<BookDetailedDto> getBooksBorrowedByMember(String memberName);

    Set<String> getDistinctBorrowedBookNames();

    Map<String, Long> getDistinctBorrowedBookNamesWithAmount();
}
