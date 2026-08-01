package ru.github.musiccrossing.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    IMAGE(".webp"),
    MP3(".mp3"),
    WAV(".wav"),
    AAC(".aac"),
    FLAC(".flac"),
    OGG(".ogg");

    private final String extension;
}
