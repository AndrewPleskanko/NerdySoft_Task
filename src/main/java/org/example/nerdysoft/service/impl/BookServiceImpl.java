package org.example.nerdysoft.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.nerdysoft.model.dto.BookDto;
import org.example.nerdysoft.model.entity.Book;
import org.example.nerdysoft.model.entity.Member;
import org.example.nerdysoft.output.persistent.BookRepository;
import org.example.nerdysoft.output.persistent.MemberRepository;
import org.example.nerdysoft.service.BookService;
import org.example.nerdysoft.service.mapper.BookMapper;
import org.example.nerdysoft.service.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        return bookRepository.findById(id).map(bookMapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public BookDto saveBook(BookDto bookDto) {
        log.info("Saving book: {}", bookDto);
        Book book = bookMapper.toEntity(bookDto);
        Book existingBook = bookRepository.findByNameAndAuthor(book.getTitle(), book.getAuthor());
        if (existingBook != null) {
            existingBook.setAmount(existingBook.getAmount() + 1);
            return bookMapper.toDto(bookRepository.save(existingBook));
        }
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (!book.getBorrowers().isEmpty()) {
            throw new IllegalStateException("Book is currently borrowed and cannot be deleted");
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        log.info("Member with id: {} borrowing book with id: {}", memberId, bookId);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("Member not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new IllegalArgumentException("Book not found"));

        if (book.getAmount() <= 0) {
            throw new IllegalStateException("Book is not available");
        }

        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new IllegalStateException("Borrow limit reached");
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
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("Member not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new IllegalArgumentException("Book not found"));

        if (!member.getBorrowedBooks().contains(book)) {
            throw new IllegalStateException("Book not borrowed by member");
        }

        book.setAmount(book.getAmount() + 1);
        member.getBorrowedBooks().remove(book);
        bookRepository.save(book);
        memberRepository.save(member);
    }

    @Override
    public Set<BookDto> getBooksBorrowedByMember(String memberName) {
        log.info("Fetching books borrowed by member: {}", memberName);
        Member member = memberRepository.findByName(memberName);
        return member.getBorrowedBooks().stream().map(bookMapper::toDto).collect(Collectors.toSet());
    }

    public Set<String> getDistinctBorrowedBookNames() {
        return bookRepository.findDistinctBorrowedBookNames();
    }

    public Map<String, Long> getDistinctBorrowedBookNamesWithAmount() {
        return bookRepository.findDistinctBorrowedBookNamesWithAmount();
    }
}