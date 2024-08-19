package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class BookNotAvailableException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BookNotAvailableException(String message) {
        super(message);
    }
}
