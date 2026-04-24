package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.AuthException;

public class PasswordResetTokenNotFound extends AuthException {
    public PasswordResetTokenNotFound() {
        super("Токен для восстановления пароля не был найден", HttpStatus.NOT_FOUND);
    }
}
