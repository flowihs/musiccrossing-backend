package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class GoogleAccountConflictException extends AuthException {
    public GoogleAccountConflictException() {
        super("Email уже зарегистрирован, но с другим Google аакнаутом", HttpStatus.CONFLICT);
    }
}
