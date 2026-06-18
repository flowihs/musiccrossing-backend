package ru.github.musiccrossing.music.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.SoundException;

public class SoundNotFoundException extends SoundException {
    public SoundNotFoundException() {
        super("Музыка не была найдена", HttpStatus.NOT_FOUND);
    }
}
