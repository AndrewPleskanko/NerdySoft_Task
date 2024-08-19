package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class BookNotFoundException extends ResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }
}
