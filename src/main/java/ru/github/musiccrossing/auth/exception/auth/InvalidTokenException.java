package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException() {
        super("Отсутствует или некорректный токен", HttpStatus.BAD_REQUEST);
    }
}
