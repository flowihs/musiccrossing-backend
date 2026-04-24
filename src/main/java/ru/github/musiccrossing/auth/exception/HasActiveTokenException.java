package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class HasActiveTokenException extends AuthException {
    public HasActiveTokenException() {
        super("Операция временно недоступна", HttpStatus.BAD_REQUEST);
    }
}
