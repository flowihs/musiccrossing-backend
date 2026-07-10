package ru.github.musiccrossing.music.dto.response;

import java.util.UUID;

public record AlbumCreateResponse(UUID id, String albumName, String artistName) {
}
