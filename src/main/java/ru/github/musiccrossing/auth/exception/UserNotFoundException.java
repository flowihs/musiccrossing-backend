package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException() {
        super("Пользователь не был найден", HttpStatus.BAD_REQUEST);
    }
}
