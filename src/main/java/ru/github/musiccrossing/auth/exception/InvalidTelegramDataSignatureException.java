package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class InvalidTelegramDataSignatureException extends AuthException {
    public InvalidTelegramDataSignatureException() {
        super("Недействительная подпись данных Telegram", HttpStatus.BAD_REQUEST);
    }
}
