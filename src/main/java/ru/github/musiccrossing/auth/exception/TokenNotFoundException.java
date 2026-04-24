package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class TokenNotFoundException extends AuthException {
    public TokenNotFoundException() {
        super("Токен не был найден", HttpStatus.NOT_FOUND);
    }
}
