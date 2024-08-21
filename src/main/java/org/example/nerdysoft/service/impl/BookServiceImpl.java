package org.example.nerdysoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.nerdysoft.constant.MessageConstants;
import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.dto.BookMainInfoDto;
import org.example.nerdysoft.model.dto.BorrowRequestDto;
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

/**
 * Implementation of the {@link BookService} interface.
 * Provides methods for managing books and members in the library system.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookMapper bookMapper;

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    /**
     * Fetches all books with their borrowers.
     *
     * @return a list of {@link BookDetailedDto} containing detailed information about all books.
     */
    @Override
    public List<BookDetailedDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAllWithBorrowers().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Fetches a book by its ID with its borrowers.
     *
     * @param id the ID of the book to fetch.
     * @return a {@link BookDetailedDto} containing detailed information about the book.
     * @throws BookNotFoundException if the book with the specified ID is not found.
     */
    @Override
    public BookDetailedDto getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        return bookRepository.findByIdWithBorrowers(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + id));
    }

    /**
     * Saves a new book or updates the amount of an existing book.
     *
     * @param bookDetailedDto the book details to save.
     * @return a {@link BookDetailedDto} containing detailed information about the saved book.
     */
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

    /**
     * Updates the details of an existing book.
     *
     * @param id the ID of the book to update.
     * @param bookDetails the new details of the book.
     * @return a {@link BookDetailedDto} containing detailed information about the updated book.
     * @throws BookNotFoundException if the book with the specified ID is not found.
     */
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

    /**
     * Deletes a book by its ID.
     *
     * @param id the ID of the book to delete.
     * @throws BookNotFoundException if the book with the specified ID is not found.
     * @throws BookNotReturnedException if the book is currently borrowed by any member.
     */
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

    /**
     * Allows a member to borrow a book.
     *
     * @param borrowRequest the borrow request containing member ID and book ID.
     * @throws MemberNotFoundException if the member with the specified ID is not found.
     * @throws BookNotFoundException if the book with the specified ID is not found.
     * @throws BookNotAvailableException if the book is not available for borrowing.
     * @throws BorrowLimitExceededException if the member has reached the borrow limit.
     */
    @Override
    @Transactional
    public void borrowBook(BorrowRequestDto borrowRequest) {
        Long memberId = borrowRequest.getMemberId();
        Long bookId = borrowRequest.getBookId();
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

        member.getBorrowedBooks().add(book);

        book.setAmount(book.getAmount() - 1);

        bookRepository.save(book);
        memberRepository.save(member);
    }

    /**
     * Allows a member to return a borrowed book.
     *
     * @param borrowRequest the borrow request containing member ID and book ID.
     * @throws MemberNotFoundException if the member with the specified ID is not found.
     * @throws BookNotFoundException if the book with the specified ID is not found.
     * @throws BookNotReturnedException if the book was not borrowed by the member.
     */
    @Override
    @Transactional
    public void returnBook(BorrowRequestDto borrowRequest) {
        Long memberId = borrowRequest.getMemberId();
        Long bookId = borrowRequest.getBookId();
        log.info("Member with id: {} returning book with id: {}", memberId, bookId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + memberId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(MessageConstants.BOOK_NOT_FOUND_MESSAGE + bookId));

        if (!member.getBorrowedBooks().contains(book)) {
            throw new BookNotReturnedException(MessageConstants.BOOK_NOT_BORROWED_BY_MEMBER_MESSAGE
                    + " " + member.getName());
        }

        member.getBorrowedBooks().remove(book);
        book.setAmount(book.getAmount() + 1);

        bookRepository.save(book);
        memberRepository.save(member);
    }

    /**
     * Fetches the books borrowed by a member.
     *
     * @param memberName the name of the member.
     * @return a set of {@link BookMainInfoDto} containing main information about the borrowed books.
     * @throws MemberNotFoundException if the member with the specified name is not found.
     */
    @Override
    public Set<BookMainInfoDto> getBooksBorrowedByMember(String memberName) {
        log.info("Fetching books borrowed by member: {}", memberName);
        Optional<Member> member = memberRepository.findByNameWithBorrowedBooks(memberName);
        if (member.isEmpty()) {
            throw new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_BY_NAME_MESSAGE + memberName);
        }
        return member.get().getBorrowedBooks().stream().map(bookMapper::toMainInfoDto).collect(Collectors.toSet());
    }

    /**
     * Fetches the distinct names of borrowed books.
     *
     * @return a set of distinct borrowed book names.
     */
    @Override
    public Set<String> getDistinctBorrowedBookNames() {
        return bookRepository.findDistinctBorrowedBookNames();
    }

    /**
     * Fetches the distinct names of borrowed books along with their amounts.
     *
     * @return a map of book names to their borrowed amounts.
     */
    @Override
    public Map<String, Long> getDistinctBorrowedBookNamesWithAmount() {
        List<Object[]> results = bookRepository.findDistinctBorrowedBookNamesWithAmountRaw();
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            String title = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            map.put(title, count);
        }
        return map;
    }
}
