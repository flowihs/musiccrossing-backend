package ru.github.musiccrossing.music.dto.response;

import ru.github.musiccrossing.storage.FileType;

import java.util.UUID;

public record SoundResponseDto(
        UUID id,
        String name,
        Integer trackNumber,
        FileType fileType,
        String s3Url,
        UUID albumId,
        String albumName
) {
}
