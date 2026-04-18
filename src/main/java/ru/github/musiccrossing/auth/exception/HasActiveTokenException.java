package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class HasActiveTokenException extends AuthException {
    public HasActiveTokenException(String number) {
        super("В следующий раз вы можете попробовать восстановить пароль через: " + number, HttpStatus.BAD_REQUEST);
    }
}
