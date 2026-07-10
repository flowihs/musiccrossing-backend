package ru.github.musiccrossing.storage.service;

import ru.github.musiccrossing.storage.FileType;

import java.util.UUID;

public interface StorageService {
    String upload(byte[] data, UUID key, FileType filetype);
}
