package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.PlaylistException;

public class PlaylistNotFoundException extends PlaylistException {
    public PlaylistNotFoundException() {
        super("Плейлист не был найден", HttpStatus.NOT_FOUND);
    }
}
