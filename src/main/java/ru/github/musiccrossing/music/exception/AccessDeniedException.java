package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ru.github.musiccrossing.common.error.exception.SoundException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
