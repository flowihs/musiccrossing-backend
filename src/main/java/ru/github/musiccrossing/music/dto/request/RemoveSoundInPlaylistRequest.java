package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RemoveSoundInPlaylistRequest {
    @NotBlank
    private Long playlistId;

    @NotBlank
    private UUID soundId;
}
