CREATE TABLE member_books
(
    member_id BIGINT NOT NULL,
    book_id   BIGINT NOT NULL,
    CONSTRAINT fk_member_books_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_member_books_book FOREIGN KEY (book_id) REFERENCES books (id),
    PRIMARY KEY (member_id, book_id)
);