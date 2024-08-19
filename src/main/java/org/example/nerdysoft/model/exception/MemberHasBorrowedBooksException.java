package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class MemberHasBorrowedBooksException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MemberHasBorrowedBooksException(String message) {
        super(message);
    }
}
