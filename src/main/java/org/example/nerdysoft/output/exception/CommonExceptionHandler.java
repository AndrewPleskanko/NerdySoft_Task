package org.example.nerdysoft.output.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INVALID_EMAIL_PASSWORD_MESSAGE =
            "The email or password you entered is incorrect. Please try again.";
    private static final String UNAUTHORIZED_MESSAGE =
            "Unauthorized: Please log in to access this resource.";
}
