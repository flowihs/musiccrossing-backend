package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class InvalidPasswordException extends AuthException {
    public InvalidPasswordException() {
        super("Неверный пароль", HttpStatus.BAD_REQUEST);
    }
}
