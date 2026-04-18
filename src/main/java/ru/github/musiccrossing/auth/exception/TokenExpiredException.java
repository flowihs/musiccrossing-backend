package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthException {
    public TokenExpiredException() {
        super("Токен устек", HttpStatus.BAD_REQUEST);
    }
}
