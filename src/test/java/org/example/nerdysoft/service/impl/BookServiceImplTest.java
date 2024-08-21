package org.example.nerdysoft.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.example.nerdysoft.constant.MessageConstants;
import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.dto.BorrowRequestDto;
import org.example.nerdysoft.model.entity.Book;
import org.example.nerdysoft.model.entity.Member;
import org.example.nerdysoft.model.exception.BookNotAvailableException;
import org.example.nerdysoft.model.exception.BookNotFoundException;
import org.example.nerdysoft.model.exception.BookNotReturnedException;
import org.example.nerdysoft.model.exception.BorrowLimitExceededException;
import org.example.nerdysoft.output.persistent.BookRepository;
import org.example.nerdysoft.output.persistent.MemberRepository;
import org.example.nerdysoft.service.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    private Book book;
    private Member member;
    private BookDetailedDto bookDetailedDto;
    private BorrowRequestDto borrowRequestDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setAmount(10);
        book.setBorrowers(new HashSet<>());

        member = new Member();
        member.setId(1L);
        member.setName("Test Member");
        member.setBorrowedBooks(new HashSet<>());

        bookDetailedDto = new BookDetailedDto();
        bookDetailedDto.setTitle("Test Book");
        bookDetailedDto.setAuthor("Test Author");
        bookDetailedDto.setAmount(10);

        borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setBookId(1L);
        borrowRequestDto.setMemberId(1L);

        bookService = new BookServiceImpl(bookRepository, memberRepository, bookMapper);
    }

    @Test
    void getAllBooks() {
        when(bookRepository.findAllWithBorrowers()).thenReturn(Collections.singletonList(book));
        when(bookMapper.toDto(book)).thenReturn(bookDetailedDto);

        List<BookDetailedDto> books = bookService.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findAllWithBorrowers();
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    void getBookById() {
        when(bookRepository.findByIdWithBorrowers(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDetailedDto);

        BookDetailedDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(bookDetailedDto, result);
    }

    @Test
    void getBookById_ThrowsBookNotFoundException() {
        when(bookRepository.findByIdWithBorrowers(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));

        assertEquals(MessageConstants.BOOK_NOT_FOUND_MESSAGE + 1L, exception.getMessage());
        verify(bookRepository, times(1)).findByIdWithBorrowers(1L);
    }

    @Test
    void saveBook_NewBook() {
        when(bookRepository.findByNameAndAuthor("Test Book", "Test Author")).thenReturn(null);
        when(bookMapper.toEntity(bookDetailedDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDetailedDto);

        BookDetailedDto result = bookService.saveBook(bookDetailedDto);

        assertNotNull(result);
        assertEquals(bookDetailedDto, result);
        verify(bookRepository, times(1)).findByNameAndAuthor("Test Book", "Test Author");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void saveBook_ExistingBook() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Test Author");
        existingBook.setAmount(5);
        existingBook.setBorrowers(new HashSet<>());

        when(bookRepository.findByNameAndAuthor("Test Book", "Test Author")).thenReturn(existingBook);
        when(bookMapper.toEntity(bookDetailedDto)).thenReturn(existingBook); // Convert DTO to entity
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toDto(existingBook)).thenReturn(bookDetailedDto); // Convert entity back to DTO

        BookDetailedDto result = bookService.saveBook(bookDetailedDto);

        assertNotNull(result);
        assertEquals(bookDetailedDto, result);
        assertEquals(10, existingBook.getAmount());
        verify(bookRepository, times(1)).findByNameAndAuthor("Test Book", "Test Author");
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void updateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDetailedDto);

        BookDetailedDto result = bookService.updateBook(1L, bookDetailedDto);

        assertNotNull(result);
        assertEquals(bookDetailedDto, result);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_ThrowsBookNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.updateBook(1L, bookDetailedDto));

        assertEquals(MessageConstants.BOOK_NOT_FOUND_MESSAGE + 1L, exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void deleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_ThrowsBookNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));

        assertEquals(MessageConstants.BOOK_NOT_FOUND_MESSAGE + 1L, exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void deleteBook_ThrowsBookNotReturnedException() {
        book.getBorrowers().add(member);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookNotReturnedException exception = assertThrows(BookNotReturnedException.class, () ->
                bookService.deleteBook(1L));

        assertEquals(MessageConstants.BOOK_NOT_RETURNED_MESSAGE, exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void borrowBook_ThrowsBookNotAvailableException() {
        book.setAmount(0);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookNotAvailableException exception = assertThrows(BookNotAvailableException.class, () ->
                bookService.borrowBook(borrowRequestDto));

        assertEquals(MessageConstants.BOOK_NOT_AVAILABLE_MESSAGE, exception.getMessage());
        verify(bookRepository, times(0)).save(book);
        verify(memberRepository, times(0)).save(member);
    }

    @Test
    void borrowBook_ThrowsBorrowLimitExceededException() {
        member.getBorrowedBooks().addAll(Collections.nCopies(10, book));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BorrowLimitExceededException exception = assertThrows(BorrowLimitExceededException.class, () ->
                bookService.borrowBook(borrowRequestDto));

        assertEquals(MessageConstants.BORROW_LIMIT_REACHED_MESSAGE, exception.getMessage());
        verify(bookRepository, times(0)).save(book);
        verify(memberRepository, times(0)).save(member);
    }

    @Test
    void returnBook() {
        member.getBorrowedBooks().add(book);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(memberRepository.save(member)).thenReturn(member);

        bookService.returnBook(borrowRequestDto);

        assertEquals(11, book.getAmount());
        assertFalse(member.getBorrowedBooks().contains(book));
        verify(bookRepository, times(1)).save(book);
        verify(memberRepository, times(1)).save(member);
    }
}