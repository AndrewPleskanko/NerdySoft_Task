package org.example.nerdysoft.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.dto.BookMainInfoDto;
import org.example.nerdysoft.model.dto.BorrowRequestDto;

public interface BookService {
    List<BookDetailedDto> getAllBooks();

    BookDetailedDto updateBook(Long id, BookDetailedDto bookDetails);

    BookDetailedDto getBookById(Long id);

    BookDetailedDto saveBook(BookDetailedDto bookDetailedDto);

    void deleteBook(Long id);

    void borrowBook(BorrowRequestDto borrowRequest);

    void returnBook(BorrowRequestDto borrowRequest);

    Set<BookMainInfoDto> getBooksBorrowedByMember(String memberName);

    Set<String> getDistinctBorrowedBookNames();

    Map<String, Long> getDistinctBorrowedBookNamesWithAmount();
}
