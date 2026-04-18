package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class HasActiveTokenException extends AuthException {
    public HasActiveTokenException() {
        super("Операция временно недоступна", HttpStatus.BAD_REQUEST);
    }
}
