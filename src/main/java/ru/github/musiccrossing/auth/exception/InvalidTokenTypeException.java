package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenTypeException extends AuthException {
    public InvalidTokenTypeException() {
        super("Неверный тип токена", HttpStatus.UNAUTHORIZED);
    }
}
