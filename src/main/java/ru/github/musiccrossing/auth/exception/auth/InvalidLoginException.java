package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class InvalidLoginException extends AuthException {
    public InvalidLoginException() {
        super("Неверный логин или пароль", HttpStatus.UNAUTHORIZED);
    }
}
