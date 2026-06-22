package ru.github.musiccrossing.storage.service;

public interface StorageService {
    String upload(byte[] data, String path, String filename);
}
