CREATE TABLE books
(
    id     SERIAL PRIMARY KEY,
    title  VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    amount INTEGER      NOT NULL CHECK (amount >= 0)
);
