package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class TokenExpiredException extends AuthException {
    public TokenExpiredException() {
        super("Токен устек", HttpStatus.BAD_REQUEST);
    }
}
