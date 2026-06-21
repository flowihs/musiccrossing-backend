package ru.github.musiccrossing.storage.exception;

import org.springframework.http.HttpStatus;
import ru.github.musiccrossing.common.error.exception.StorageException;

public class FileIsNotValidImage extends StorageException {
    public FileIsNotValidImage() {
        super("Загруженный файл не является валидным изображением", HttpStatus.BAD_REQUEST);
    }
}
