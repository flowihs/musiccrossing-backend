package ru.github.musiccrossing.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    IMAGE(".webp");

    private final String extension;
}
