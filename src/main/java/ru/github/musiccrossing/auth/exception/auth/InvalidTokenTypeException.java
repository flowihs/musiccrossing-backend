package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class InvalidTokenTypeException extends AuthException {
    public InvalidTokenTypeException() {
        super("Неверный тип токена", HttpStatus.UNAUTHORIZED);
    }
}
