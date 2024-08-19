package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class BookNotReturnedException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BookNotReturnedException(String message) {
        super(message);
    }
}
