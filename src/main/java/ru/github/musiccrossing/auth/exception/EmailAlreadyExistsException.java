package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AuthException {
    public EmailAlreadyExistsException() {
        super("Эта почта уже занята другим пользователем", HttpStatus.BAD_REQUEST);
    }
}
