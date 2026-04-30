package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class UsernameAlreadyExistsException extends AuthException {
    public UsernameAlreadyExistsException() {
        super("Это имя пользователя уже занято другим пользователем", HttpStatus.BAD_REQUEST);
    }
}
