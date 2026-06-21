package ru.github.musiccrossing.storage.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.StorageException;

public class FailedEncodeImageException extends StorageException {
    public FailedEncodeImageException() {
        super("Ошибка при кодировании файла", HttpStatus.BAD_REQUEST);
    }
}
