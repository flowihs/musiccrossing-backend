package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.PlaylistException;

public class PlaylistNotOwnedException extends PlaylistException {
    public PlaylistNotOwnedException() {
        super("Плейлист не принадлежит пользователю", HttpStatus.FORBIDDEN);
    }
}
