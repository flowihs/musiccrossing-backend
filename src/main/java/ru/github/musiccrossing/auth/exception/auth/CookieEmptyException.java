package ru.github.musiccrossing.auth.exception.auth;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class CookieEmptyException extends AuthException {
    public CookieEmptyException() {
        super("Cookie является пустым", HttpStatus.BAD_REQUEST);
    }
}
