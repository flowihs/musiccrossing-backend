package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends AuthException {
    public RefreshTokenExpiredException() {
        super("Refresh токен истек", HttpStatus.UNAUTHORIZED);
    }
}
