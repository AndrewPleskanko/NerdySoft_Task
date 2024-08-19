package org.example.nerdysoft.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.nerdysoft.constant.MessageConstants;
import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.entity.Book;
import org.example.nerdysoft.model.entity.Member;
import org.example.nerdysoft.model.exception.BookNotAvailableException;
import org.example.nerdysoft.model.exception.BookNotFoundException;
import org.example.nerdysoft.model.exception.BookNotReturnedException;
import org.example.nerdysoft.model.exception.BorrowLimitExceededException;
import org.example.nerdysoft.model.exception.MemberNotFoundException;
import org.example.nerdysoft.output.persistent.BookRepository;
import org.example.nerdysoft.output.persistent.MemberRepository;
import org.example.nerdysoft.service.BookService;
import org.example.nerdysoft.service.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookMapper bookMapper;

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    @Override
    public List<BookDetailedDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAllWithBorrowers().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public BookDetailedDto getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        return bookRepository.findByIdWithBorrowers(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + id));
    }

    @Override
    @Transactional
    public BookDetailedDto saveBook(BookDetailedDto bookDetailedDto) {
        log.info("Saving book: {}", bookDetailedDto);
        Book book = bookMapper.toEntity(bookDetailedDto);
        Book existingBook = bookRepository.findByNameAndAuthor(book.getTitle(), book.getAuthor());
        if (existingBook != null) {
            existingBook.setAmount(existingBook.getAmount() + book.getAmount());
            return bookMapper.toDto(bookRepository.save(existingBook));
        }
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookDetailedDto updateBook(Long id, BookDetailedDto bookDetails) {
        log.info("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setAmount(bookDetails.getAmount());

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + id));
        if (!book.getBorrowers().isEmpty()) {
            throw new BookNotReturnedException(MessageConstants.BOOK_NOT_RETURNED_MESSAGE);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        log.info("Member with id: {} borrowing book with id: {}", memberId, bookId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + memberId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + bookId));

        if (book.getAmount() <= 0) {
            throw new BookNotAvailableException(MessageConstants.BOOK_NOT_AVAILABLE_MESSAGE);
        }
        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new BorrowLimitExceededException(MessageConstants.BORROW_LIMIT_REACHED_MESSAGE);
        }

        book.setAmount(book.getAmount() - 1);
        member.getBorrowedBooks().add(book);
        bookRepository.save(book);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        log.info("Member with id: {} returning book with id: {}", memberId, bookId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + memberId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + bookId));

        if (!member.getBorrowedBooks().contains(book)) {
            throw new BookNotReturnedException(MessageConstants.BOOK_NOT_BORROWED_BY_MEMBER_MESSAGE);
        }

        book.setAmount(book.getAmount() + 1);
        member.getBorrowedBooks().remove(book);
        bookRepository.save(book);
        memberRepository.save(member);
    }

    @Override
    public Set<BookDetailedDto> getBooksBorrowedByMember(String memberName) {
        log.info("Fetching books borrowed by member: {}", memberName);
        Member member = memberRepository.findByName(memberName);
        if (member == null) {
            throw new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_BY_NAME_MESSAGE + memberName);
        }
        return member.getBorrowedBooks().stream().map(bookMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getDistinctBorrowedBookNames() {
        return bookRepository.findDistinctBorrowedBookNames();
    }

    @Override
    public Map<String, Long> getDistinctBorrowedBookNamesWithAmount() {
        return bookRepository.findDistinctBorrowedBookNamesWithAmount();
    }
}
