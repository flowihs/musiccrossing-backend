package ru.github.musiccrossing.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddSoundInPlaylistRequest {
    @NotBlank
    private Long playlistId;

    @NotBlank
    private UUID soundId;
}
