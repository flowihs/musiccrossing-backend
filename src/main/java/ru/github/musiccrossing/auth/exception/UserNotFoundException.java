package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException() {
        super("Пользователь не был найден", HttpStatus.BAD_REQUEST);
    }
}
