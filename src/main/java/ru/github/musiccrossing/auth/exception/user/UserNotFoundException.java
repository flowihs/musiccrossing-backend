package ru.github.musiccrossing.auth.exception.user;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.UserException;

public class UserNotFoundException extends UserException {
    public UserNotFoundException() {
        super("Пользователь не был найден", HttpStatus.BAD_REQUEST);
    }
}
