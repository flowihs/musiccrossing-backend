package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class GoogleAccountConflictException extends AuthException {
    public GoogleAccountConflictException() {
        super("Email уже зарегистрирован, но с другим Google аакнаутом", HttpStatus.CONFLICT);
    }
}
