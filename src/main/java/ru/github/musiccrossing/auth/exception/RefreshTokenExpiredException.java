package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class RefreshTokenExpiredException extends AuthException {
    public RefreshTokenExpiredException() {
        super("Refresh токен истек", HttpStatus.UNAUTHORIZED);
    }
}
