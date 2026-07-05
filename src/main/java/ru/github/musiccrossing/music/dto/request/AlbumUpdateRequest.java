package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AlbumUpdateRequest(
        @NotBlank String artistName,
        @NotBlank String albumName
) {
}
