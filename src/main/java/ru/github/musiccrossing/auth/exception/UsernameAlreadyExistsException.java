package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends AuthException {
    public UsernameAlreadyExistsException() {
        super("Это имя пользователя уже занято другим пользователем", HttpStatus.BAD_REQUEST);
    }
}
