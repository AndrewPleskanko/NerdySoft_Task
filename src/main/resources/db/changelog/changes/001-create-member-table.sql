CREATE TABLE members
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    membership_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);