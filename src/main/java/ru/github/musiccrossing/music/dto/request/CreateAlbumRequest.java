package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAlbumRequest(
        @NotBlank String artistName,
        @NotBlank String albumName) {
}
