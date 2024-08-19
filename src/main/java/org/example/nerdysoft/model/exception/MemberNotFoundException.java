package org.example.nerdysoft.model.exception;

import java.io.Serial;

public class MemberNotFoundException extends ResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MemberNotFoundException(String message) {
        super(message);
    }
}
