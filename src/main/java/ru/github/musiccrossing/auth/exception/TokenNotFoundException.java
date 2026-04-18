package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends AuthException {
    public TokenNotFoundException() {
        super("Токен не был найден", HttpStatus.NOT_FOUND);
    }
}
