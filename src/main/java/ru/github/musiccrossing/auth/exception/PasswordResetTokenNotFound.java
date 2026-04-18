package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class PasswordResetTokenNotFound extends AuthException {
    public PasswordResetTokenNotFound() {
        super("Токен для восстановления пароля не был найден", HttpStatus.NOT_FOUND);
    }
}
