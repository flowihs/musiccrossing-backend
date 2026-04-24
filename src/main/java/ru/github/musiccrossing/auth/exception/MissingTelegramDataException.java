package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class MissingTelegramDataException extends AuthException {
    public MissingTelegramDataException(String message) {
        super("Отсутствует поле " + message + "в данных Telegram", HttpStatus.BAD_REQUEST);
    }
}
