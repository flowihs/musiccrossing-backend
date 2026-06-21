package ru.github.musiccrossing.storage.controller;

public class FileIsNotValidImage extends RuntimeException {
    public FileIsNotValidImage() {
        super("Загруженный файл не является валидным изображением");
    }
}
