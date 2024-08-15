package org.example.nerdysoft.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.dto.BookDto;

public interface BookService {
    List<BookDto> getAllBooks();

    BookDto getBookById(Long id);

    BookDto saveBook(BookDto bookDto);

    void deleteBook(Long id);

    void borrowBook(Long memberId, Long bookId);

    void returnBook(Long memberId, Long bookId);

    Set<BookDto> getBooksBorrowedByMember(String memberName);

    Set<String> getDistinctBorrowedBookNames();

    Map<String, Long> getDistinctBorrowedBookNamesWithAmount();
}
