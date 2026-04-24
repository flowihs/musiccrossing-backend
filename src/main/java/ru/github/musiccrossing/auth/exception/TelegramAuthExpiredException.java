package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class TelegramAuthExpiredException extends AuthException {
    public TelegramAuthExpiredException() {
        super("Время сесси авторизации telegram истекло", HttpStatus.UNAUTHORIZED);
    }
}
