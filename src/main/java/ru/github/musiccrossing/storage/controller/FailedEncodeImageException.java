package ru.github.musiccrossing.storage.controller;

public class FailedEncodeImageException extends RuntimeException {
    public FailedEncodeImageException() {
        super("Ошибка при кодировании файла");
    }
}
