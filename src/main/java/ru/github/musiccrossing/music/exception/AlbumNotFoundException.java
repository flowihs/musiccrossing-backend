package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;

public class AlbumNotFoundException extends ru.github.musiccrossing.common.error.exception.SoundException {
    public AlbumNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
