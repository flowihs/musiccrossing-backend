package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class TelegramAuthExpiredException extends AuthException {
    public TelegramAuthExpiredException() {
        super("Время сесси авторизации telegram истекло", HttpStatus.UNAUTHORIZED);
    }
}
