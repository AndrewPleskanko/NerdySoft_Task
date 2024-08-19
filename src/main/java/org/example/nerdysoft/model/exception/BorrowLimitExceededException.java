package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class BorrowLimitExceededException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
