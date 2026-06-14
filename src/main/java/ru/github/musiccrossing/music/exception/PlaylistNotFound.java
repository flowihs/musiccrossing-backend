package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.PlaylistException;

public class PlaylistNotFound extends PlaylistException {
    public PlaylistNotFound() {
        super("Плейлист не был найден", HttpStatus.NOT_FOUND);
    }
}
