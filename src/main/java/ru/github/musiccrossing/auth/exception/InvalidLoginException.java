package ru.github.musiccrossing.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoginException extends AuthException {
    public InvalidLoginException() {
        super("Неверный логин или пароль", HttpStatus.UNAUTHORIZED);
    }
}
